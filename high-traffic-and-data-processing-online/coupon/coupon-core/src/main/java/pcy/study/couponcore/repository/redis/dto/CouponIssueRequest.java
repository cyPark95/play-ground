package pcy.study.couponcore.repository.redis.dto;

public record CouponIssueRequest(
        long couponId,
        long userId
) {
}
