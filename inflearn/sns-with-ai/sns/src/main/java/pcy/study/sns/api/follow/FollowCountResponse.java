package pcy.study.sns.api.follow;

public record FollowCountResponse(
        long followersCount,
        long followeesCount
) {
}
