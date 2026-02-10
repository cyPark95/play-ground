package pcy.study.server.controller.response;

import pcy.study.server.service.info.UserInfo;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String userId,
        String nickname,
        LocalDateTime createdAt
) {

    public static UserResponse from(UserInfo info) {
        return new UserResponse(
                info.id(),
                info.userId(),
                info.nickname(),
                info.createdAt()
        );
    }
}
