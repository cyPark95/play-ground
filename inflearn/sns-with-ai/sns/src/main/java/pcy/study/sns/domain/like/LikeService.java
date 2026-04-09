package pcy.study.sns.domain.like;

import pcy.study.sns.domain.base.DomainException;
import pcy.study.sns.domain.base.ErrorCode;
import pcy.study.sns.domain.post.Post;
import pcy.study.sns.domain.post.PostRepository;
import pcy.study.sns.domain.post.PostService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    @Transactional
    public Like createLike(Long postId, User user) {
        Post post = postService.getPostById(postId);

        if (likeRepository.existsByUserIdAndPostIdAndDeletedAtIsNull(user.getId(), postId)) {
            throw new DomainException(ErrorCode.ALREADY_LIKED);
        }

        Like like = Like.create(user, post);
        Like newLike = likeRepository.save(like);

        postRepository.incrementLikeCount(postId);

        return newLike;
    }

    public List<Like> getAllLikes() {
        return likeRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
    }

    public Like getLikeById(Long id) {
        return likeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DomainException(ErrorCode.LIKE_NOT_FOUND));
    }

    @Transactional
    public void deleteLike(Long id, User user) {
        Like like = getLikeById(id);

        if (!like.getUser().getId().equals(user.getId())) {
            throw new DomainException(ErrorCode.LIKE_UNAUTHORIZED_DELETE);
        }

        like.delete();
        postRepository.decrementLikeCount(like.getPost().getId());
    }

    public List<Like> getLikesByUserId(Long userId) {
        return likeRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
    }
}
