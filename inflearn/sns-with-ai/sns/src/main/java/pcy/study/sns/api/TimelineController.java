package pcy.study.sns.api;

import pcy.study.sns.api.post.PostResponse;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.post.PostService;
import pcy.study.sns.domain.timeline.TimelinePage;
import pcy.study.sns.domain.timeline.TimelineService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;
    private final PostService postService;

    @GetMapping("/api/v1/timelines")
    public ResponseEntity<TimelineResponse> getTimeline(
            @AuthUser User user,
            @RequestParam(required = false) Double cursor,
            @RequestParam(defaultValue = "20") int limit
    ) {
        TimelinePage page = timelineService.getTimeline(user, cursor, limit);
        List<PostResponse> posts = postService.enrichWithUserContext(page.posts(), user).stream()
                .map(PostResponse::from)
                .toList();
        return ResponseEntity.ok(new TimelineResponse(posts, page.nextCursor(), page.hasMore()));
    }
}
