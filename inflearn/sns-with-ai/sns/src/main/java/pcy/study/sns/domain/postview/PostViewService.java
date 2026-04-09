package pcy.study.sns.domain.postview;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewService {

    private final PostViewRepository postViewRepository;

    public void incrementPostView(Long postId) {
        postViewRepository.incrementPostView(postId);
    }

    public Long getPostView(Long postId) {
        return postViewRepository.getPostView(postId);
    }
}
