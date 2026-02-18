package pcy.study.server.controller.request;

import pcy.study.server.service.command.CommentUpdateCommand;

public record CommentModifyRequest(
        String contents
) {

    public CommentUpdateCommand toCommand(Long userId, Long commentId) {
        return new CommentUpdateCommand(
                userId,
                commentId,
                contents
        );
    }
}
