package pcy.study.server.service.command;

import java.util.List;

public record PostUpdateCommand(
        Long postId,
        String name,
        String contents,
        boolean isAdmin,
        Long userId,
        FileSaveCommand fileSaveCommand,
        List<Long> tagIds
) {
}
