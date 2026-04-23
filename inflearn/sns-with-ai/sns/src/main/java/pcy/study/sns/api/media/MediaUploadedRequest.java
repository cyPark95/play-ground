package pcy.study.sns.api.media;

import pcy.study.sns.domain.media.MultipartUploaded;

import java.util.List;

public record MediaUploadedRequest(
        Long mediaId,
        List<MultipartUploaded> parts
) {
}
