package pcy.study.server.service.info;

import pcy.study.server.domain.Post;
import pcy.study.server.domain.PostTag;

import java.time.LocalDateTime;
import java.util.List;

public record PostInfo(
        Long id,
        String name,
        String contents,
        boolean isAdmin,
        int views,
        Long userId,
        Long categoryId,
        LocalDateTime createdAt,
        Long fileId,
        List<Long> postTagIds
) {

    public static PostInfo from(Post post) {
        return new PostInfo(
                post.getId(),
                post.getName(),
                post.getContents(),
                post.isAdmin(),
                post.getViews(),
                post.getUserId(),
                post.getCategoryId(),
                post.getCreatedAt(),
                post.getFile().getId(),
                post.getPostTags().stream()
                        .map(PostTag::getId)
                        .toList()
        );
    }
}
