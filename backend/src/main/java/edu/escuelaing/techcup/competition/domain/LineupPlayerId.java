package edu.escuelaing.techcup.competition.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Composite key of {@link LineupPlayer}. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LineupPlayerId implements Serializable {

    @Column(name = "lineup_id")
    private Long lineupId;

    @Column(name = "player_user_id")
    private Long playerUserId;
}
