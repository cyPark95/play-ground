package pcy.study.server.service.info;

import pcy.study.server.domain.User;

import java.time.LocalDateTime;

public record UserInfo(
        Long id,
        String userId,
        String password,
        String nickname,
        boolean isWithDraw,
        boolean isAdmin,
        LocalDateTime createdAt
) {

    public static UserInfo from(User user) {
        return new UserInfo(
                user.getId(),
                user.getUserId(),
                user.getPassword(),
                user.getNickname(),
                user.isWithDraw(),
                user.isAdmin(),
                user.getCreatedAt()
        );
    }
}
