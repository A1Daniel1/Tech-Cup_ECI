package edu.escuelaing.techcup.players.infrastructure;

import edu.escuelaing.techcup.players.domain.PlayerProfile;
import edu.escuelaing.techcup.teams.application.MemberProfilePort;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Adapter that lets the teams module read sport profiles through its own port. */
@Component
public class MemberProfileAdapter implements MemberProfilePort {

    private final PlayerProfileRepository profiles;

    public MemberProfileAdapter(PlayerProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberProfile> findProfile(Long userId) {
        return profiles.findById(userId).map(MemberProfileAdapter::toMemberProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, MemberProfile> findProfiles(Collection<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return profiles.findAllById(userIds).stream()
                .map(MemberProfileAdapter::toMemberProfile)
                .collect(Collectors.toMap(MemberProfile::userId, Function.identity()));
    }

    private static MemberProfile toMemberProfile(PlayerProfile profile) {
        return new MemberProfile(profile.getUserId(), profile.getPosition(), profile.getJerseyNumber(),
                profile.getPhotoFileId());
    }
}
