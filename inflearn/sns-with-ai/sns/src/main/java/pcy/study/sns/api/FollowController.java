package pcy.study.sns.api;

import pcy.study.sns.api.follow.FollowRequest;
import pcy.study.sns.api.follow.FollowResponse;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.follow.Follow;
import pcy.study.sns.domain.follow.FollowService;
import pcy.study.sns.domain.user.User;
import pcy.study.sns.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    @PostMapping("/api/v1/follows")
    public ResponseEntity<FollowResponse> follow(
            @RequestBody FollowRequest request,
            @AuthUser User user
    ) {
        Follow follow = followService.follow(user, request.followeeId());
        User followee = userService.getById(follow.getFolloweeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(FollowResponse.from(follow, user, followee));
    }

    @DeleteMapping("/api/v1/follows")
    public ResponseEntity<Void> unfollow(
            @RequestBody FollowRequest request,
            @AuthUser User user
    ) {
        followService.unfollow(user, request.followeeId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/follows/followers")
    public ResponseEntity<List<FollowResponse>> getFollowers(@AuthUser User user) {
        List<Follow> followers = followService.getFollowers(user);
        List<FollowResponse> responses = followers.stream()
                .map(follow -> FollowResponse.from(
                        follow,
                        userService.getById(follow.getFollowerId()),
                        user
                ))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/v1/follows/followees")
    public ResponseEntity<List<FollowResponse>> getFollowees(@AuthUser User user) {
        List<Follow> followees = followService.getFollowees(user);
        List<FollowResponse> responses = followees.stream()
                .map(follow -> FollowResponse.from(
                        follow,
                        user,
                        userService.getById(follow.getFolloweeId())
                ))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/v1/follows/check/{followeeId}")
    public ResponseEntity<Boolean> isFollowing(
            @PathVariable Long followeeId,
            @AuthUser User user
    ) {
        return ResponseEntity.ok(followService.isFollowing(user, followeeId));
    }
}
