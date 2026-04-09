package pcy.study.sns.api.like;

import pcy.study.sns.domain.like.Like;

import java.time.LocalDateTime;

public record LikeResponse(
        Long id,
        Long userId,
        String username,
        Long postId,
        String postContent,
        LocalDateTime createdAt
) {
    public static LikeResponse from(Like like) {
        return new LikeResponse(
                like.getId(),
                like.getUser().getId(),
                like.getUser().getUsername(),
                like.getPost().getId(),
                like.getPost().getContent(),
                like.getCreatedAt()
        );
    }
}
