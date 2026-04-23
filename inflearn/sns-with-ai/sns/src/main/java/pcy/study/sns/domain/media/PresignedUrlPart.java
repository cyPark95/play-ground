package pcy.study.sns.domain.media;

public record PresignedUrlPart(
        int partNumber,
        String presignedUrl
) {
}
