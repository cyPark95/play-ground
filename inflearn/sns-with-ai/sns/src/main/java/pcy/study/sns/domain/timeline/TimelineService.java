package pcy.study.sns.domain.timeline;

import pcy.study.sns.domain.follow.Follow;
import pcy.study.sns.domain.follow.FollowCount;
import pcy.study.sns.domain.follow.FollowCountService;
import pcy.study.sns.domain.follow.FollowRepository;
import pcy.study.sns.domain.post.Post;
import pcy.study.sns.domain.post.PostRepository;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TimelineRepository timelineRepository;
    private final FollowRepository followRepository;
    private final FollowCountService followCountService;
    private final PostRepository postRepository;

    public void fanOutToFollowers(Long postId, User author) {
        FollowCount followCount = followCountService.getFollowCount(author.getId());

        timelineRepository.addPostToTimeline(author.getId(), postId);

        if (followCount.isCeleb()) {
            timelineRepository.addCelebPost(author.getId(), postId);
            return;
        }

        List<Follow> follows = followRepository.findByFolloweeIdAndDeletedAtIsNull(author.getId());
        follows.parallelStream()
                .forEach(follow -> timelineRepository.addPostToTimeline(follow.getFollowerId(), postId));
    }

    public TimelinePage getTimeline(User user, Double cursor, int limit) {
        List<Follow> follows = followRepository.findByFollowerIdAndDeletedAtIsNull(user.getId());
        follows.parallelStream()
                .map(Follow::getFolloweeId)
                .map(followCountService::getFollowCount)
                .filter(FollowCount::isCeleb)
                .flatMap(followCount -> timelineRepository.getCelebPosts(followCount.getUserId(), 5).stream())
                .forEach(postId -> timelineRepository.addPostToTimelineIfAbsent(user.getId(), postId));

        List<TimelineEntry> entries = timelineRepository.getTimeline(user.getId(), cursor, limit);

        if (entries.isEmpty()) {
            return new TimelinePage(List.of(), null, false);
        }

        List<Long> postIds = entries.stream().map(TimelineEntry::postId).toList();
        Map<Long, Post> postMap = postRepository.findAllByIdInAndDeletedAtIsNull(postIds).stream()
                .collect(toMap(Post::getId, Function.identity()));

        List<Post> posts = entries.stream()
                .map(entry -> postMap.get(entry.postId()))
                .filter(Objects::nonNull)
                .toList();

        Double nextCursor = entries.getLast().score();
        boolean hasMore = entries.size() >= limit;

        return new TimelinePage(posts, nextCursor, hasMore);
    }
}
