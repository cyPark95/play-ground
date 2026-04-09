package pcy.study.sns.api.repost;

import pcy.study.sns.domain.post.Post;

import java.time.LocalDateTime;

public record RepostResponse(
        Long id,
        Long userId,
        String username,
        Long userProfileMediaId,
        Long repostId,
        LocalDateTime createdAt
) {
    public static RepostResponse from(Post repost) {
        return new RepostResponse(
                repost.getId(),
                repost.getUser().getId(),
                repost.getUser().getUsername(),
                repost.getUser().getProfileMediaId(),
                repost.getRepostId(),
                repost.getCreatedAt()
        );
    }
}
