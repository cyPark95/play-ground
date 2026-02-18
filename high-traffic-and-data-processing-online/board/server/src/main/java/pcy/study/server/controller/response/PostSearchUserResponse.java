package pcy.study.server.controller.response;

import pcy.study.server.service.info.PostSearchUserInfo;

public record PostSearchUserResponse(
        Long id,
        String userId,
        String nickname,
        boolean isWithDraw,
        boolean isAdmin
) {

    public static PostSearchUserResponse from(PostSearchUserInfo info) {
        return new PostSearchUserResponse(
                info.getId(),
                info.getUserId(),
                info.getNickname(),
                info.isWithDraw(),
                info.isAdmin()
        );
    }
}
