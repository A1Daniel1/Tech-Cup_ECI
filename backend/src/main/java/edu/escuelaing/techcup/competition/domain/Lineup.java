package edu.escuelaing.techcup.competition.domain;

import edu.escuelaing.techcup.teams.domain.Team;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The lineup a captain submits for one team of one match: a formation plus exactly
 * {@value #STARTERS} starters; every other member of the team is listed as a substitute.
 */
@Entity
@Table(name = "lineups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lineup {

    /** Football 7: goalkeeper plus six outfield players. */
    public static final int STARTERS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Formation formation = Formation.F_2_3_1;

    @OneToMany(mappedBy = "lineup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LineupPlayer> players = new ArrayList<>();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void replacePlayers(List<LineupPlayer> newPlayers) {
        players.clear();
        newPlayers.forEach(player -> {
            player.setLineup(this);
            players.add(player);
        });
    }

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
