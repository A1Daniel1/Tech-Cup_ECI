package edu.escuelaing.techcup.tournaments.application;

import edu.escuelaing.techcup.identity.application.UserService;
import edu.escuelaing.techcup.shared.audit.AuditAction;
import edu.escuelaing.techcup.shared.audit.AuditService;
import edu.escuelaing.techcup.shared.exception.BusinessRuleException;
import edu.escuelaing.techcup.shared.exception.NotFoundException;
import edu.escuelaing.techcup.shared.security.AuthenticatedUser;
import edu.escuelaing.techcup.shared.storage.FileKind;
import edu.escuelaing.techcup.shared.storage.FileStorage;
import edu.escuelaing.techcup.shared.storage.FileUpload;
import edu.escuelaing.techcup.tournaments.api.dto.CreateTournamentRequest;
import edu.escuelaing.techcup.tournaments.api.dto.TournamentResponse;
import edu.escuelaing.techcup.tournaments.api.dto.UpdateTournamentRequest;
import edu.escuelaing.techcup.tournaments.api.dto.VenueResponse;
import edu.escuelaing.techcup.tournaments.domain.RegistrationStatus;
import edu.escuelaing.techcup.tournaments.domain.Tournament;
import edu.escuelaing.techcup.tournaments.domain.TournamentStatus;
import edu.escuelaing.techcup.tournaments.domain.Venue;
import edu.escuelaing.techcup.tournaments.infrastructure.RegistrationRepository;
import edu.escuelaing.techcup.tournaments.infrastructure.TournamentRepository;
import edu.escuelaing.techcup.tournaments.infrastructure.VenueRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Tournament use cases (spec 7.4). Rules enforced here:
 * <ul>
 *   <li>Created in {@link TournamentStatus#DRAFT}; only a DRAFT may be edited or deleted.</li>
 *   <li>Dates must be coherent: registration deadline &le; start date &le; end date.</li>
 *   <li>Activate (DRAFT &rarr; ACTIVE) requires an uploaded rulebook and at least one venue.</li>
 *   <li>Start (ACTIVE &rarr; IN_PROGRESS) is allowed only on the start date and with at least
 *       {@value #MIN_TEAMS_TO_START} approved registrations.</li>
 *   <li>Finish (IN_PROGRESS &rarr; FINISHED) is allowed once the end date has been reached or the
 *       FINAL match has been played ({@link FinalMatchPort}).</li>
 *   <li>The rulebook is a PDF and venues carry an optional image; both go through the
 *       {@link FileStorage} port. Venues may not be touched once the tournament is FINISHED.</li>
 * </ul>
 * The shape of the lifecycle itself is owned by the {@link TournamentStatus} State machine; this
 * service only adds the guards that need collaborators. Every mutation is audited.
 */
@Service
public class TournamentService {

    static final String ENTITY_TYPE = "TOURNAMENT";
    static final String VENUE_ENTITY_TYPE = "VENUE";
    static final int MIN_TEAMS_TO_START = 2;

    private static final Set<TournamentStatus> LIVE_STATUSES =
            Set.of(TournamentStatus.ACTIVE, TournamentStatus.IN_PROGRESS);

    private final TournamentRepository tournaments;
    private final VenueRepository venues;
    private final RegistrationRepository registrations;
    private final UserService userService;
    private final FileStorage fileStorage;
    private final FinalMatchPort finalMatch;
    private final AuditService auditService;
    private final Clock clock;

    public TournamentService(TournamentRepository tournaments, VenueRepository venues,
                             RegistrationRepository registrations, UserService userService,
                             FileStorage fileStorage, FinalMatchPort finalMatch, AuditService auditService,
                             Clock clock) {
        this.tournaments = tournaments;
        this.venues = venues;
        this.registrations = registrations;
        this.userService = userService;
        this.fileStorage = fileStorage;
        this.finalMatch = finalMatch;
        this.auditService = auditService;
        this.clock = clock;
    }

    // --- queries ---------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<TournamentResponse> list() {
        return tournaments.findAllByOrderByStartDateDescIdDesc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TournamentResponse get(Long id) {
        return toResponse(requireTournament(id));
    }

    /** The latest ACTIVE or IN_PROGRESS tournament, i.e. the one the platform is showing today. */
    @Transactional(readOnly = true)
    public Optional<TournamentResponse> findCurrent() {
        return currentTournament().map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<Tournament> currentTournament() {
        return tournaments.findFirstByStatusInOrderByStartDateDescIdDesc(LIVE_STATUSES);
    }

    @Transactional(readOnly = true)
    public Tournament requireTournament(Long id) {
        return tournaments.findById(id).orElseThrow(() -> NotFoundException.of("el torneo", id));
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> venuesOf(Long tournamentId) {
        return venues.findByTournamentIdOrderByIdAsc(tournamentId).stream().map(VenueResponse::from).toList();
    }

    /** Ids of the approved teams, in registration order. Used by the fixture generator. */
    @Transactional(readOnly = true)
    public List<Long> approvedTeamIds(Long tournamentId) {
        return registrations.findTeamIdsByStatus(tournamentId, RegistrationStatus.APPROVED);
    }

    // --- lifecycle -------------------------------------------------------------------------

    @Transactional
    public TournamentResponse create(AuthenticatedUser actor, CreateTournamentRequest request) {
        validateDates(request.registrationDeadline(), request.startDate(), request.endDate());
        Tournament tournament = tournaments.save(Tournament.builder()
                .name(request.name().trim())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .registrationDeadline(request.registrationDeadline())
                .maxTeams(request.maxTeams())
                .fee(request.fee())
                .status(TournamentStatus.DRAFT)
                .createdBy(userService.getUser(actor.id()))
                .build());
        auditService.record(actor.id(), AuditAction.TOURNAMENT_CREATED, ENTITY_TYPE, tournament.getId(),
                Map.of("name", tournament.getName(), "startDate", tournament.getStartDate().toString()));
        return toResponse(tournament);
    }

    @Transactional
    public TournamentResponse update(AuthenticatedUser actor, Long id, UpdateTournamentRequest request) {
        Tournament tournament = requireTournament(id);
        requireEditable(tournament);

        Map<String, Object> changes = new HashMap<>();
        if (request.name() != null && !request.name().isBlank()) {
            tournament.setName(request.name().trim());
            changes.put("name", tournament.getName());
        }
        if (request.startDate() != null) {
            tournament.setStartDate(request.startDate());
            changes.put("startDate", request.startDate().toString());
        }
        if (request.endDate() != null) {
            tournament.setEndDate(request.endDate());
            changes.put("endDate", request.endDate().toString());
        }
        if (request.registrationDeadline() != null) {
            tournament.setRegistrationDeadline(request.registrationDeadline());
            changes.put("registrationDeadline", request.registrationDeadline().toString());
        }
        if (request.maxTeams() != null) {
            tournament.setMaxTeams(request.maxTeams());
            changes.put("maxTeams", request.maxTeams());
        }
        if (request.fee() != null) {
            tournament.setFee(request.fee());
            changes.put("fee", request.fee().toPlainString());
        }
        if (changes.isEmpty()) {
            throw new BusinessRuleException("No hay nada que actualizar: indique al menos un campo.");
        }
        validateDates(tournament.getRegistrationDeadline(), tournament.getStartDate(), tournament.getEndDate());

        auditService.record(actor.id(), AuditAction.TOURNAMENT_UPDATED, ENTITY_TYPE, id, changes);
        return toResponse(tournament);
    }

    @Transactional
    public void delete(AuthenticatedUser actor, Long id) {
        Tournament tournament = requireTournament(id);
        requireEditable(tournament);
        tournaments.delete(tournament);
        auditService.record(actor.id(), AuditAction.TOURNAMENT_DELETED, ENTITY_TYPE, id,
                Map.of("name", tournament.getName()));
    }

    /** DRAFT &rarr; ACTIVE. Requires the rulebook and at least one venue so teams know what they join. */
    @Transactional
    public TournamentResponse activate(AuthenticatedUser actor, Long id) {
        Tournament tournament = requireTournament(id);
        if (!tournament.hasRulebook()) {
            throw new BusinessRuleException("Cargue el reglamento en PDF antes de activar el torneo.");
        }
        if (venues.findByTournamentIdOrderByIdAsc(id).isEmpty()) {
            throw new BusinessRuleException("Registre al menos una cancha antes de activar el torneo.");
        }
        tournament.moveTo(TournamentStatus.ACTIVE);
        auditService.record(actor.id(), AuditAction.TOURNAMENT_ACTIVATED, ENTITY_TYPE, id);
        return toResponse(tournament);
    }

    /** ACTIVE &rarr; IN_PROGRESS, only on the start date and with enough approved teams. */
    @Transactional
    public TournamentResponse start(AuthenticatedUser actor, Long id) {
        Tournament tournament = requireTournament(id);
        LocalDate today = LocalDate.now(clock);
        if (!today.equals(tournament.getStartDate())) {
            throw new BusinessRuleException("El torneo solo se puede iniciar en su fecha de inicio ("
                    + tournament.getStartDate() + "); hoy es " + today + ".");
        }
        long approved = registrations.countByTournamentIdAndStatus(id, RegistrationStatus.APPROVED);
        if (approved < MIN_TEAMS_TO_START) {
            throw new BusinessRuleException("Se requieren al menos " + MIN_TEAMS_TO_START
                    + " inscripciones aprobadas para iniciar el torneo (por ahora hay " + approved + ").");
        }
        tournament.moveTo(TournamentStatus.IN_PROGRESS);
        auditService.record(actor.id(), AuditAction.TOURNAMENT_STARTED, ENTITY_TYPE, id,
                Map.of("approvedTeams", approved));
        return toResponse(tournament);
    }

    /**
     * IN_PROGRESS &rarr; FINISHED. Allowed once the end date has been reached, or earlier when the
     * FINAL match has already been played (the competition is over, whatever the calendar says).
     */
    @Transactional
    public TournamentResponse finish(AuthenticatedUser actor, Long id) {
        Tournament tournament = requireTournament(id);
        LocalDate today = LocalDate.now(clock);
        boolean endDateReached = !today.isBefore(tournament.getEndDate());
        boolean finalPlayed = finalMatch.isFinalMatchPlayed(id);
        if (!endDateReached && !finalPlayed) {
            throw new BusinessRuleException("El torneo no se puede finalizar antes de su fecha de cierre ("
                    + tournament.getEndDate() + ") a menos que ya se haya jugado la final.");
        }
        tournament.moveTo(TournamentStatus.FINISHED);
        auditService.record(actor.id(), AuditAction.TOURNAMENT_FINISHED, ENTITY_TYPE, id,
                Map.of("endDateReached", endDateReached, "finalPlayed", finalPlayed));
        return toResponse(tournament);
    }

    // --- rulebook and venues ---------------------------------------------------------------

    @Transactional
    public TournamentResponse uploadRulebook(AuthenticatedUser actor, Long id, MultipartFile file) {
        Tournament tournament = requireTournament(id);
        if (tournament.getStatus() != TournamentStatus.DRAFT && tournament.getStatus() != TournamentStatus.ACTIVE) {
            throw new BusinessRuleException("El reglamento solo se puede cargar mientras el torneo esté en borrador o activo.");
        }
        String fileId = fileStorage.store(FileUpload.from(file), FileKind.PDF);
        tournament.setRulebookFileId(fileId);
        auditService.record(actor.id(), AuditAction.RULEBOOK_UPLOADED, ENTITY_TYPE, id,
                Map.of("rulebookFileId", fileId));
        return toResponse(tournament);
    }

    @Transactional
    public VenueResponse createVenue(AuthenticatedUser actor, Long id, String name, String description,
                                     MultipartFile image) {
        Tournament tournament = requireTournament(id);
        requireNotFinished(tournament, "las canchas");
        String imageFileId = image == null || image.isEmpty()
                ? null
                : fileStorage.store(FileUpload.from(image), FileKind.IMAGE);
        Venue venue = Venue.builder()
                .name(name.trim())
                .description(description == null || description.isBlank() ? null : description.trim())
                .imageFileId(imageFileId)
                .build();
        tournament.addVenue(venue);
        venue = venues.save(venue);
        auditService.record(actor.id(), AuditAction.VENUE_CREATED, VENUE_ENTITY_TYPE, venue.getId(),
                Map.of("tournamentId", id, "name", venue.getName()));
        return VenueResponse.from(venue);
    }

    @Transactional
    public void deleteVenue(AuthenticatedUser actor, Long id, Long venueId) {
        Tournament tournament = requireTournament(id);
        requireNotFinished(tournament, "las canchas");
        Venue venue = venues.findById(venueId)
                .filter(candidate -> candidate.getTournament().getId().equals(id))
                .orElseThrow(() -> NotFoundException.of("la cancha", venueId));
        tournament.removeVenue(venue);
        venues.delete(venue);
        auditService.record(actor.id(), AuditAction.VENUE_DELETED, VENUE_ENTITY_TYPE, venueId,
                Map.of("tournamentId", id, "name", venue.getName()));
    }

    // --- helpers ---------------------------------------------------------------------------

    public TournamentResponse toResponse(Tournament tournament) {
        long approved = tournament.getId() == null
                ? 0L
                : registrations.countByTournamentIdAndStatus(tournament.getId(), RegistrationStatus.APPROVED);
        return new TournamentResponse(
                tournament.getId(),
                tournament.getName(),
                tournament.getStartDate(),
                tournament.getEndDate(),
                tournament.getRegistrationDeadline(),
                tournament.getMaxTeams(),
                tournament.getFee(),
                tournament.getStatus(),
                tournament.getRulebookFileId(),
                tournament.getVenues().stream().map(VenueResponse::from).toList(),
                approved);
    }

    private static void requireEditable(Tournament tournament) {
        if (!tournament.getStatus().isEditable()) {
            throw new BusinessRuleException("Un torneo solo se puede modificar o eliminar mientras esté en borrador; "
                    + "su estado actual es «" + tournament.getStatus().label() + "».");
        }
    }

    /** @param what the Spanish noun phrase for what is being protected, e.g. {@code "las canchas"} */
    private static void requireNotFinished(Tournament tournament, String what) {
        if (tournament.getStatus().isFinished()) {
            throw new BusinessRuleException("No se pueden modificar " + what + " de un torneo finalizado.");
        }
    }

    static void validateDates(LocalDate registrationDeadline, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessRuleException("La fecha de inicio no puede ser posterior a la fecha de cierre.");
        }
        if (registrationDeadline.isAfter(startDate)) {
            throw new BusinessRuleException("La fecha límite de inscripción no puede ser posterior a la fecha de inicio.");
        }
    }
}
