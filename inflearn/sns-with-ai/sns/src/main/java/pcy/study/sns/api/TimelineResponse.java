package pcy.study.sns.api;

import pcy.study.sns.api.post.PostResponse;

import java.util.List;

public record TimelineResponse(
        List<PostResponse> posts,
        Double nextCursor,
        boolean hasMore
) {
}
