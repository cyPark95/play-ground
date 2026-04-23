package pcy.study.sns.api.media;

import pcy.study.sns.domain.media.MediaType;

import java.util.Map;

public record MediaCreateRequest(
        MediaType mediaType,
        String path,
        Long postId,
        Map<String, Object> attributes
) {
}
