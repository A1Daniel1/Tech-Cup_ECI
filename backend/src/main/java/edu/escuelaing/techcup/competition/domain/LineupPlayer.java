package edu.escuelaing.techcup.competition.domain;

import edu.escuelaing.techcup.identity.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** One player of a {@link Lineup}, either a starter or a substitute. */
@Entity
@Table(name = "lineup_players")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineupPlayer {

    @EmbeddedId
    private LineupPlayerId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("lineupId")
    @JoinColumn(name = "lineup_id")
    private Lineup lineup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("playerUserId")
    @JoinColumn(name = "player_user_id")
    private AppUser player;

    @Column(nullable = false)
    private boolean starter;

    public static LineupPlayer of(AppUser player, boolean starter) {
        return LineupPlayer.builder()
                .id(new LineupPlayerId(null, player.getId()))
                .player(player)
                .starter(starter)
                .build();
    }

    public Long playerId() {
        return player.getId();
    }
}
