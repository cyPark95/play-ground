package pcy.study.couponapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pcy.study.couponapi.controller.dto.CouponIssueRequest;
import pcy.study.couponapi.controller.dto.CouponIssueResponse;
import pcy.study.couponapi.service.CouponIssueRequestService;

@RestController
@RequiredArgsConstructor
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/v1/issue")
    public CouponIssueResponse issue(@RequestBody CouponIssueRequest body) {
        couponIssueRequestService.issueRequest(body);
        return new CouponIssueResponse(true, null);
    }

    @PostMapping("/v1/issue-async")
    public CouponIssueResponse asyncIssue(@RequestBody CouponIssueRequest body) {
        couponIssueRequestService.asyncIssueRequest(body);
        return new CouponIssueResponse(true, null);
    }
}
