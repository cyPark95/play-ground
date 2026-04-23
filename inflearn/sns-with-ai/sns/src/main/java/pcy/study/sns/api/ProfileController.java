package pcy.study.sns.api;

import pcy.study.sns.api.media.MediaInitResponse;
import pcy.study.sns.api.post.PostResponse;
import pcy.study.sns.api.reply.ReplyResponse;
import pcy.study.sns.api.user.ProfileImageInitRequest;
import pcy.study.sns.api.user.ProfileImageUpdateRequest;
import pcy.study.sns.api.user.UserResponse;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.like.LikeService;
import pcy.study.sns.domain.media.PresignedUrl;
import pcy.study.sns.domain.post.PostService;
import pcy.study.sns.domain.post.PostWithViewCount;
import pcy.study.sns.domain.user.User;
import pcy.study.sns.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final PostService postService;
    private final LikeService likeService;
    private final UserService userService;

    @GetMapping("/api/v1/profile/posts")
    public ResponseEntity<List<PostResponse>> getMyPosts(@AuthUser User user) {
        List<PostWithViewCount> postsWithViewCount = new ArrayList<>(postService.getPostsByUserId(user.getId()));

        List<PostResponse> posts = postsWithViewCount.stream()
                .sorted(comparing(pvc -> pvc.post().getCreatedAt(), reverseOrder()))
                .map(pvc -> postService.enrichWithUserContext(pvc.post(), user))
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/api/v1/profile/replies")
    public ResponseEntity<List<ReplyResponse>> getMyReplies(@AuthUser User user) {
        List<ReplyResponse> replies = postService.getRepliesByUserId(user.getId()).stream()
                .map(ReplyResponse::from)
                .toList();
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/api/v1/profile/likes")
    public ResponseEntity<List<PostResponse>> getMyLikedPosts(@AuthUser User user) {
        List<PostResponse> posts = likeService.getLikesByUserId(user.getId()).stream()
                .map(like -> postService.enrichWithUserContext(like.getPost(), user))
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/api/v1/profile/image/init")
    public ResponseEntity<MediaInitResponse> initProfileImage(@AuthUser User user, @RequestBody ProfileImageInitRequest request) {
        PresignedUrl presignedUrl = userService.initProfileImage(request.fileSize(), user);
        return ResponseEntity.ok(MediaInitResponse.from(presignedUrl));
    }

    @PostMapping("/api/v1/profile/image/uploaded")
    public ResponseEntity<UserResponse> uploadedProfileImage(@AuthUser User user, @RequestBody ProfileImageUpdateRequest request) {
        User updatedUser = userService.updateProfileImage(request.mediaId(), user);
        String profileImageUrl = userService.getProfileImageUrl(updatedUser);
        return ResponseEntity.ok(UserResponse.from(updatedUser, profileImageUrl));
    }
}
