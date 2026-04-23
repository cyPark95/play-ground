package pcy.study.sns.domain.media;

public record MultipartUploaded(
        int partNumber,
        String eTag
) {
}
