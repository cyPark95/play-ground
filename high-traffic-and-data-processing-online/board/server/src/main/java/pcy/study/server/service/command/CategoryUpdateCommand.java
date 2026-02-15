package pcy.study.server.service.command;

public record CategoryUpdateCommand(
        Long userId,
        Long categoryId,
        String name
) {
}
