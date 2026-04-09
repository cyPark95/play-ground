package pcy.study.sns.domain.postview;

import pcy.study.sns.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostViewSyncService {

    private final PostViewRepository postViewRepository;
    private final PostRepository postRepository;

    @Transactional
    public void syncPostView(Long postId) {
        Long postView = postViewRepository.getPostView(postId);

        postRepository.findByIdAndDeletedAtIsNull(postId).ifPresent(post -> {
            post.updateViewCount(postView);
            postRepository.save(post);
        });

        postViewRepository.removeDirtyPostId(postId);
    }
}
