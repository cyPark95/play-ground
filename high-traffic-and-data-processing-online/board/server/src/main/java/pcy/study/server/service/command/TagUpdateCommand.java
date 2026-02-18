package pcy.study.server.service.command;

public record TagUpdateCommand(
        Long userId,
        Long tagId,
        String name,
        String url
) {
}
