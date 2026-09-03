package edu.escuelaing.techcup.teams.application;

import edu.escuelaing.techcup.players.domain.Position;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Sport-profile facts the teams module needs about its members. Implemented by the players
 * module ({@code players.infrastructure.MemberProfileAdapter}).
 */
public interface MemberProfilePort {

    record MemberProfile(Long userId, Position position, int jerseyNumber, String photoFileId) {
    }

    Optional<MemberProfile> findProfile(Long userId);

    Map<Long, MemberProfile> findProfiles(Collection<Long> userIds);
}
