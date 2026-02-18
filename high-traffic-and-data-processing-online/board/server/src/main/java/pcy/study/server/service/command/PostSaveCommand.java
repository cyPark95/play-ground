package pcy.study.server.service.command;

import pcy.study.server.domain.Post;
import pcy.study.server.domain.PostTag;

import java.util.List;

public record PostSaveCommand(
        String name,
        String contents,
        boolean isAdmin,
        Long userId,
        Long categoryId,
        FileSaveCommand fileSaveCommand,
        List<Long> tagIds
) {

    public Post toDomain() {
        return new Post(
                name,
                contents,
                isAdmin,
                userId,
                categoryId,
                fileSaveCommand.toDomain(),
                tagIds.stream()
                        .map(PostTag::new)
                        .toList()
        );
    }
}
