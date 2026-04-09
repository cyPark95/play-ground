package pcy.study.sns.api.reply;

import pcy.study.sns.domain.post.Post;
import pcy.study.sns.domain.post.PostWithViewCount;

import java.time.LocalDateTime;

public record ReplyResponse(
        Long id,
        String content,
        Long userId,
        String username,
        Long userProfileMediaId,
        Long parentId,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static ReplyResponse from(Post post) {
        return new ReplyResponse(
                post.getId(),
                post.getContent(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getUser().getProfileMediaId(),
                post.getParentId(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }

    public static ReplyResponse from(PostWithViewCount postWithViewCount) {
        return from(postWithViewCount.post());
    }
}
