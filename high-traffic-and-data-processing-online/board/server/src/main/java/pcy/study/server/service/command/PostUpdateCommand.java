package pcy.study.server.service.command;

public record PostUpdateCommand(
        Long id,
        String name,
        String contents,
        boolean isAdmin,
        int views,
        Long userId,
        Long fileId
) {
}
