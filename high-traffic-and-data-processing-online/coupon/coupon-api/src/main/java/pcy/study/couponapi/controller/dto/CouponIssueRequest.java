package pcy.study.couponapi.controller.dto;

public record CouponIssueRequest(
        long couponId,
        long userId
) {
}
