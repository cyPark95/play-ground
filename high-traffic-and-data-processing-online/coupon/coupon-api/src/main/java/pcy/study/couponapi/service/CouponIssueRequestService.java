package pcy.study.couponapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pcy.study.couponapi.controller.dto.CouponIssueRequest;
import pcy.study.couponcore.service.AsyncCouponIssueService;
import pcy.study.couponcore.service.CouponIssueService;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final AsyncCouponIssueService asyncCouponIssueService;

    public void issueRequest(CouponIssueRequest request) {
        couponIssueService.issue(request.couponId(), request.userId());
        log.info("쿠폰 발급 완료. coupon: {}, userId: {}", request.couponId(), request.userId());
    }

    public void asyncIssueRequest(CouponIssueRequest request) {
        asyncCouponIssueService.issue(request.couponId(), request.userId());
    }
}
