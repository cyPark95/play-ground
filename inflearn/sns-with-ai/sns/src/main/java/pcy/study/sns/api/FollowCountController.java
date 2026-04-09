package pcy.study.sns.api;

import pcy.study.sns.api.follow.FollowCountResponse;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.follow.FollowCount;
import pcy.study.sns.domain.follow.FollowCountService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowCountController {

    private final FollowCountService followCountService;

    @GetMapping("/api/v1/follow_counts")
    public ResponseEntity<FollowCountResponse> getFollowCount(@AuthUser User user) {
        FollowCount followCount = followCountService.getFollowCount(user.getId());
        return ResponseEntity.ok(new FollowCountResponse(followCount.getFollowersCount(), followCount.getFolloweesCount()));
    }

    @GetMapping("/api/v1/users/{userId}/follow_counts")
    public ResponseEntity<FollowCountResponse> getUserFollowCount(@PathVariable Long userId) {
        FollowCount followCount = followCountService.getFollowCountOrDefault(userId);
        return ResponseEntity.ok(new FollowCountResponse(followCount.getFollowersCount(), followCount.getFolloweesCount()));
    }
}
