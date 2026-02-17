package pcy.study.server.controller.response;

import pcy.study.server.service.info.PostInfo;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String name,
        String contents,
        boolean isAdmin,
        int views,
        Long userId,
        String userNickname,
        Long categoryId,
        String categoryName,
        Long fileId,
        LocalDateTime createdAt
) {

    public static PostResponse from(PostInfo info) {
        return new PostResponse(
                info.id(),
                info.name(),
                info.contents(),
                info.isAdmin(),
                info.views(),
                info.userId(),
                info.userNickname(),
                info.categoryId(),
                info.categoryName(),
                info.fileId(),
                info.createdAt()
        );
    }
}
