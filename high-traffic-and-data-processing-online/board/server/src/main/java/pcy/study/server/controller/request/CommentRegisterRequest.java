package pcy.study.server.controller.request;

import pcy.study.server.service.command.CommentSaveCommand;

public record CommentRegisterRequest(
        String contents,
        Long subCommentId
) {

    public CommentSaveCommand toCommand(Long userId, Long postId) {
        return new CommentSaveCommand(
                userId,
                postId,
                contents,
                subCommentId
        );
    }
}
