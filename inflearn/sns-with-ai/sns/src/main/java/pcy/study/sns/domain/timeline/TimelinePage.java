package pcy.study.sns.domain.timeline;

import pcy.study.sns.domain.post.Post;

import java.util.List;

public record TimelinePage(
        List<Post> posts,
        Double nextCursor,
        boolean hasMore
) {
}
