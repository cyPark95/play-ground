package pcy.study.sns.api.user;

import pcy.study.sns.domain.user.User;

public record UserResponse(
        Long id,
        String username,
        Long profileMediaId,
        String profileImageUrl
) {
    public static UserResponse from(User user, String profileImageUrl) {
        return new UserResponse(user.getId(), user.getUsername(), user.getProfileMediaId(), profileImageUrl);
    }
}
