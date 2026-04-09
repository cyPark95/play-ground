package pcy.study.sns.domain.repost;

import pcy.study.sns.domain.base.DomainException;
import pcy.study.sns.domain.base.ErrorCode;
import pcy.study.sns.domain.post.Post;
import pcy.study.sns.domain.post.PostRepository;
import pcy.study.sns.domain.timeline.TimelineService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepostService {

    private final PostRepository postRepository;
    private final TimelineService timelineService;

    @Transactional
    public Post createRepost(Long repostId, User user) {
        postRepository.findByIdAndDeletedAtIsNull(repostId)
                .orElseThrow(() -> new DomainException(ErrorCode.ORIGINAL_POST_NOT_FOUND_FOR_REPOST));

        if (postRepository.existsByUserIdAndRepostIdAndDeletedAtIsNull(user.getId(), repostId)) {
            throw new DomainException(ErrorCode.ALREADY_REPOSTED);
        }

        Post repost = Post.createRepost(user, repostId);
        Post savedRepost = postRepository.save(repost);

        postRepository.incrementRepostCount(repostId);

        timelineService.fanOutToFollowers(savedRepost.getId(), user);

        return savedRepost;
    }

    public List<Post> getAllReposts() {
        return postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc().stream()
                .filter(post -> post.getRepostId() != null)
                .toList();
    }

    public Post getRepostById(Long id) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DomainException(ErrorCode.REPOST_NOT_FOUND));
        if (post.getRepostId() == null) {
            throw new DomainException(ErrorCode.NOT_A_REPOST);
        }
        return post;
    }

    @Transactional
    public void deleteRepost(Long id, User user) {
        Post repost = getRepostById(id);

        if (!repost.getUser().getId().equals(user.getId())) {
            throw new DomainException(ErrorCode.REPOST_UNAUTHORIZED_DELETE);
        }

        repost.delete();
        postRepository.decrementRepostCount(repost.getRepostId());
    }
}
