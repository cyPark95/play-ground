package pcy.study.server.controller.response;

import pcy.study.server.service.info.PostInfo;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String name,
        String contents,
        boolean isAdmin,
        int views,
        Long userId,
        Long categoryId,
        LocalDateTime createdAt,
        Long fileId,
        List<Long> postTagIds
) {

    public static PostResponse from(PostInfo info) {
        return new PostResponse(
                info.id(),
                info.name(),
                info.contents(),
                info.isAdmin(),
                info.views(),
                info.userId(),
                info.categoryId(),
                info.createdAt(),
                info.fileId(),
                info.postTagIds()
        );
    }
}
