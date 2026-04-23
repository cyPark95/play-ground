package pcy.study.sns.api.media;

import pcy.study.sns.domain.media.Media;
import pcy.study.sns.domain.media.PresignedUrl;
import pcy.study.sns.domain.media.MediaStatus;
import pcy.study.sns.domain.media.MediaType;
import pcy.study.sns.domain.media.PresignedUrlPart;

import java.time.LocalDateTime;
import java.util.List;

public record MediaInitResponse(
        Long id,
        MediaType mediaType,
        String path,
        MediaStatus status,
        Long userId,
        String presignedUrl,
        String uploadId,
        List<PresignedUrlPart> presignedUrlParts,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static MediaInitResponse from(PresignedUrl presignedUrl) {
        Media media = presignedUrl.media();

        return new MediaInitResponse(
                media.getId(),
                media.getMediaType(),
                media.getPath(),
                media.getStatus(),
                media.getUserId(),
                presignedUrl.presignedUrl(),
                presignedUrl.uploadId(),
                presignedUrl.presignedUrlParts(),
                media.getCreatedAt(),
                media.getModifiedAt()
        );
    }
}
