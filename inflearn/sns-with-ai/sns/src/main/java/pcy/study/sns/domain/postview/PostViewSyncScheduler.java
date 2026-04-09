package pcy.study.sns.domain.postview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewSyncScheduler {

    private final PostViewRepository postViewRepository;
    private final PostViewSyncService postViewSyncService;

    @Scheduled(fixedRate = 60000)
    public void syncPostViewsToDatabase() {
        Set<Long> dirtyPostIds = postViewRepository.getDirtyPostIds();

        if (dirtyPostIds.isEmpty()) {
            return;
        }

        log.info("Syncing post views for {} posts", dirtyPostIds.size());

        dirtyPostIds.forEach(postId -> {
            try {
                postViewSyncService.syncPostView(postId);
            } catch (Exception e) {
                log.error("Failed to sync view count for post {}", postId, e);
            }
        });
    }
}
