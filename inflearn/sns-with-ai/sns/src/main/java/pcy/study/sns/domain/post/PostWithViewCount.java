package pcy.study.sns.domain.post;

public record PostWithViewCount(
        Post post,
        Long viewCount
) {
}
