package pcy.study.sns.api;

import pcy.study.sns.api.repost.RepostCreateRequest;
import pcy.study.sns.api.repost.RepostResponse;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.post.Post;
import pcy.study.sns.domain.repost.RepostService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RepostController {

    private final RepostService repostService;

    @PostMapping("/api/v1/reposts")
    public ResponseEntity<RepostResponse> createRepost(
            @RequestBody RepostCreateRequest request,
            @AuthUser User user
    ) {
        Post repost = repostService.createRepost(request.postId(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(RepostResponse.from(repost));
    }

    @GetMapping("/api/v1/reposts")
    public ResponseEntity<List<RepostResponse>> getAllReposts() {
        List<RepostResponse> reposts = repostService.getAllReposts().stream()
                .map(RepostResponse::from)
                .toList();
        return ResponseEntity.ok(reposts);
    }

    @GetMapping("/api/v1/reposts/{id}")
    public ResponseEntity<RepostResponse> getRepostById(@PathVariable Long id) {
        Post repost = repostService.getRepostById(id);
        return ResponseEntity.ok(RepostResponse.from(repost));
    }

    @DeleteMapping("/api/v1/reposts/{id}")
    public ResponseEntity<Void> deleteRepost(
            @PathVariable Long id,
            @AuthUser User user
    ) {
        repostService.deleteRepost(id, user);
        return ResponseEntity.noContent().build();
    }
}
