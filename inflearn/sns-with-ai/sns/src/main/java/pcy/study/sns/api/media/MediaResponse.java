package pcy.study.sns.api.media;

import pcy.study.sns.domain.media.Media;
import pcy.study.sns.domain.media.MediaStatus;
import pcy.study.sns.domain.media.MediaType;

import java.time.LocalDateTime;
import java.util.Map;

public record MediaResponse(
        Long id,
        MediaType mediaType,
        String path,
        MediaStatus status,
        Long userId,
        Long fileSize,
        Map<String, Object> attributes,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static MediaResponse from(Media media) {
        return new MediaResponse(
                media.getId(),
                media.getMediaType(),
                media.getPath(),
                media.getStatus(),
                media.getUserId(),
                media.getFileSize(),
                media.getAttributes(),
                media.getCreatedAt(),
                media.getModifiedAt()
        );
    }
}
