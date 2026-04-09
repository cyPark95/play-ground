package pcy.study.sns.domain.postview;

import java.util.Set;

public interface PostViewRepository {

    void incrementPostView(Long postId);

    Long getPostView(Long postId);

    Set<Long> getDirtyPostIds();

    void removeDirtyPostId(Long postId);
}
