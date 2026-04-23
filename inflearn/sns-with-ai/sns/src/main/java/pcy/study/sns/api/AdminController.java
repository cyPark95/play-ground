package pcy.study.sns.api;

import pcy.study.sns.domain.media.Media;
import pcy.study.sns.domain.media.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final MediaRepository mediaRepository;

    @GetMapping("/api/v1/admin/media/parts")
    public ResponseEntity<List<MultipartMediaResponse>> getMediaByPartNumber(@RequestParam int partNumber) {
        String partNumberJson = "[{\"partNumber\": " + partNumber + "}]";
        List<MultipartMediaResponse> responses = mediaRepository.findByPartNumber(partNumberJson).stream()
                .map(MultipartMediaResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    public record MultipartMediaResponse(
            Long id,
            String mediaType,
            String path,
            String status,
            Long userId,
            String uploadId,
            Long fileSize,
            int partsCount,
            Map<String, Object> attributes,
            String createdAt,
            String modifiedAt
    ) {
        @SuppressWarnings("unchecked")
        public static MultipartMediaResponse from(Media media) {
            int partsCount = 0;
            if (media.getAttributes() != null && media.getAttributes().get("parts") instanceof List<?> parts) {
                partsCount = parts.size();
            }
            return new MultipartMediaResponse(
                    media.getId(),
                    media.getMediaType().name(),
                    media.getPath(),
                    media.getStatus().name(),
                    media.getUserId(),
                    media.getUploadId(),
                    media.getFileSize(),
                    partsCount,
                    media.getAttributes(),
                    media.getCreatedAt().toString(),
                    media.getModifiedAt().toString()
            );
        }
    }
}
