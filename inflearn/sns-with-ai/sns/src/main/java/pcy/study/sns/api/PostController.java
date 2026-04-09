package pcy.study.sns.api;

import pcy.study.sns.api.post.PostCreateRequest;
import pcy.study.sns.api.post.PostResponse;
import pcy.study.sns.api.post.PostUpdateRequest;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.post.Post;
import pcy.study.sns.domain.post.PostService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/api/v1/posts")
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostCreateRequest request,
            @AuthUser User user
    ) {
        Post post = postService.createPost(request.content(), request.mediaIds(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(PostResponse.from(post));
    }

    @GetMapping("/api/v1/posts")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts().stream()
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/api/v1/posts/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long id,
            @AuthUser User user
    ) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(PostResponse.from(postService.enrichWithUserContext(post, user)));
    }

    @PutMapping("/api/v1/posts/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody PostUpdateRequest request,
            @AuthUser User user
    ) {
        Post post = postService.updatePost(id, request.content(), user);
        return ResponseEntity.ok(PostResponse.from(post));
    }

    @DeleteMapping("/api/v1/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthUser User user
    ) {
        postService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }
}
