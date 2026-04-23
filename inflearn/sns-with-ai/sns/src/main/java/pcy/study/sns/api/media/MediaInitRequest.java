package pcy.study.sns.api.media;

import pcy.study.sns.domain.media.MediaType;

public record MediaInitRequest(
        MediaType mediaType,
        Long fileSize
) {
}
