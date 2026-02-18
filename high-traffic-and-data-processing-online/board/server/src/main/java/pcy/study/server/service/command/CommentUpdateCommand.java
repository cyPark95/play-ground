package pcy.study.server.service.command;

public record CommentUpdateCommand(
        Long userId,
        Long commentId,
        String contents
) {
}
