package pcy.study.sns.domain.media;

import java.util.List;

public record MultipartUploadInfo(
        String uploadId,
        List<PresignedUrlPart> presignedUrlParts
) {
}
