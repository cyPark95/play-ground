package pcy.study.sns.api;

import pcy.study.sns.api.media.MediaInitRequest;
import pcy.study.sns.api.media.MediaInitResponse;
import pcy.study.sns.api.media.MediaResponse;
import pcy.study.sns.api.media.MediaUploadedRequest;
import pcy.study.sns.api.media.PresignedUrlResponse;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.media.Media;
import pcy.study.sns.domain.media.PresignedUrl;
import pcy.study.sns.domain.media.MediaService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/api/v1/media/init")
    public ResponseEntity<MediaInitResponse> initMedia(
            @RequestBody MediaInitRequest request,
            @AuthUser User user
    ) {
        PresignedUrl result = mediaService.initMedia(request.mediaType(), request.fileSize(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(MediaInitResponse.from(result));
    }

    @PostMapping("/api/v1/media/uploaded")
    public ResponseEntity<MediaResponse> mediaUploaded(
            @RequestBody MediaUploadedRequest request,
            @AuthUser User user
    ) {
        Media media = mediaService.mediaUploaded(request.mediaId(), request.parts(), user);
        return ResponseEntity.ok(MediaResponse.from(media));
    }

    @GetMapping("/api/v1/media/{id}")
    public ResponseEntity<MediaResponse> getMediaById(@PathVariable Long id) {
        Media media = mediaService.getMediaById(id);
        return ResponseEntity.ok(MediaResponse.from(media));
    }

    @GetMapping("/api/v1/media/{id}/presigned-url")
    public ResponseEntity<PresignedUrlResponse> getPresignedUrl(@PathVariable Long id) {
        Media media = mediaService.getMediaById(id);
        String presignedUrl = mediaService.getPresignedUrl(media);
        return ResponseEntity.ok(new PresignedUrlResponse(presignedUrl, MediaResponse.from(media)));
    }

    @GetMapping("/api/v1/users/{userId}/media")
    public ResponseEntity<List<MediaResponse>> getMediaByUserId(@PathVariable Long userId) {
        List<MediaResponse> mediaList = mediaService.getMediaByUserId(userId).stream()
                .map(MediaResponse::from)
                .toList();
        return ResponseEntity.ok(mediaList);
    }

    @DeleteMapping("/api/v1/media/{id}")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable Long id,
            @AuthUser User user
    ) {
        mediaService.deleteMedia(id, user);
        return ResponseEntity.noContent().build();
    }
}
