package edu.escuelaing.techcup.teams.domain;

import edu.escuelaing.techcup.identity.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Membership of a user in a team. The captain is always a member. */
@Entity
@Table(name = "team_members")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {

    @EmbeddedId
    private TeamMemberId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    static TeamMember of(Team team, AppUser user) {
        return TeamMember.builder()
                .id(new TeamMemberId(team.getId(), user.getId()))
                .team(team)
                .user(user)
                .joinedAt(Instant.now())
                .build();
    }

    public Long userId() {
        return user.getId();
    }
}
