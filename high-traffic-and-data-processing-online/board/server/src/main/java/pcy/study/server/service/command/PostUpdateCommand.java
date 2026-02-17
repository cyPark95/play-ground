package pcy.study.server.service.command;

public record PostUpdateCommand(
        Long postId,
        String name,
        String contents,
        boolean isAdmin,
        Long userId,
        FileSaveCommand fileSaveCommand
) {
}
