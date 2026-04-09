package pcy.study.sns.domain.post;

public record PostWithUserContext(
        Post post,
        Long viewCount,
        Boolean isLikedByMe,
        Long likeIdByMe,
        Boolean isRepostedByMe,
        Long repostIdByMe,
        Post repostedPost,
        Post quotedPost,
        Post parentPost,
        Long repostedByUserId,
        String repostedByUsername
) {
}
