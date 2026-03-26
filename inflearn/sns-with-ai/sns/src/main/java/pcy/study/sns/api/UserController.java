package pcy.study.sns.api;

import pcy.study.sns.api.post.PostResponse;
import pcy.study.sns.api.reply.ReplyResponse;
import pcy.study.sns.api.user.UserResponse;
import pcy.study.sns.api.user.UserSignupRequest;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.post.PostService;
import pcy.study.sns.domain.post.PostWithViewCount;
import pcy.study.sns.domain.user.User;
import pcy.study.sns.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @PostMapping("/api/v1/users/signup")
    public ResponseEntity<UserResponse> signupUser(@RequestBody UserSignupRequest request) {
        User user = userService.signup(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user, null));
    }

    @GetMapping("/api/v1/users/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthUser User user) {
        String profileImageUrl = userService.getProfileImageUrl(user);
        return ResponseEntity.ok(UserResponse.from(user, profileImageUrl));
    }

    @GetMapping("/api/v1/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        User user = userService.getById(userId);
        String profileImageUrl = userService.getProfileImageUrl(user);
        return ResponseEntity.ok(UserResponse.from(user, profileImageUrl));
    }

    @GetMapping("/api/v1/users/{userId}/posts")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long userId, @AuthUser User currentUser) {
        List<PostWithViewCount> postsWithViewCount = postService.getPostsByUserId(userId);

        List<PostResponse> posts = postsWithViewCount.stream()
                .sorted(comparing(pvc -> pvc.post().getCreatedAt(), reverseOrder()))
                .map(pvc -> postService.enrichWithUserContext(pvc.post(), currentUser))
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/api/v1/users/{userId}/replies")
    public ResponseEntity<List<ReplyResponse>> getUserReplies(@PathVariable Long userId) {
        List<ReplyResponse> replies = postService.getRepliesByUserId(userId).stream()
                .map(ReplyResponse::from)
                .toList();
        return ResponseEntity.ok(replies);
    }
}
