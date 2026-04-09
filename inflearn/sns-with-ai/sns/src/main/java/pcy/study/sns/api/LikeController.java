package pcy.study.sns.api;

import pcy.study.sns.api.like.LikeCreateRequest;
import pcy.study.sns.api.like.LikeResponse;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.like.Like;
import pcy.study.sns.domain.like.LikeService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/v1/likes")
    public ResponseEntity<LikeResponse> createLike(
            @RequestBody LikeCreateRequest request,
            @AuthUser User user
    ) {
        Like like = likeService.createLike(request.postId(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(LikeResponse.from(like));
    }

    @GetMapping("/api/v1/likes")
    public ResponseEntity<List<LikeResponse>> getAllLikes() {
        List<LikeResponse> likes = likeService.getAllLikes().stream()
                .map(LikeResponse::from)
                .toList();
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/api/v1/likes/{id}")
    public ResponseEntity<LikeResponse> getLikeById(@PathVariable Long id) {
        Like like = likeService.getLikeById(id);
        return ResponseEntity.ok(LikeResponse.from(like));
    }

    @DeleteMapping("/api/v1/likes/{id}")
    public ResponseEntity<Void> deleteLike(
            @PathVariable Long id,
            @AuthUser User user
    ) {
        likeService.deleteLike(id, user);
        return ResponseEntity.noContent().build();
    }
}
