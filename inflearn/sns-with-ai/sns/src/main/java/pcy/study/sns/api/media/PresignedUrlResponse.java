package pcy.study.sns.api.media;

public record PresignedUrlResponse(
        String presignedUrl,
        MediaResponse media
) {
}
