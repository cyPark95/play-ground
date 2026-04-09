package pcy.study.sns.api.follow;

import pcy.study.sns.domain.follow.Follow;
import pcy.study.sns.domain.user.User;

import java.time.LocalDateTime;

public record FollowResponse(
        Long id,
        Long followerId,
        String followerUsername,
        Long followeeId,
        String followeeUsername,
        LocalDateTime createdAt
) {
    public static FollowResponse from(Follow follow, User follower, User followee) {
        return new FollowResponse(
                follow.getId(),
                follow.getFollowerId(),
                follower.getUsername(),
                follow.getFolloweeId(),
                followee.getUsername(),
                follow.getCreatedAt()
        );
    }
}
