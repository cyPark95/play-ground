package pcy.study.server.service.command;

import pcy.study.server.domain.Post;

public record PostSaveCommand(
        String name,
        String contents,
        boolean isAdmin,
        Long userId,
        Long categoryId,
        Long fileId
) {

    public Post toDomain() {
        return new Post(
                name,
                contents,
                isAdmin,
                userId,
                categoryId,
                fileId
        );
    }
}
