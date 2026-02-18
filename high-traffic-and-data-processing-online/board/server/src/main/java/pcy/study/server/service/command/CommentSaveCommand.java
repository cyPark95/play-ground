package pcy.study.server.service.command;

import pcy.study.server.domain.Comment;

public record CommentSaveCommand(
        Long userId,
        Long postId,
        String content,
        Long subCommentId
) {

    public Comment toDomain(Long postId) {
        return new Comment(
                postId,
                content,
                subCommentId
        );
    }
}
