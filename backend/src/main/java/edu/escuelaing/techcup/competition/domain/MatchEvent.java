package edu.escuelaing.techcup.competition.domain;

import edu.escuelaing.techcup.identity.domain.AppUser;
import edu.escuelaing.techcup.teams.domain.Team;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A goal or a card attributed to one player of one of the two teams of a match. */
@Entity
@Table(name = "match_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_user_id", nullable = false)
    private AppUser player;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EventType type;

    /** Minute of the match; optional because referees do not always report it. */
    private Integer minute;

    public boolean isGoal() {
        return type == EventType.GOAL;
    }
}
