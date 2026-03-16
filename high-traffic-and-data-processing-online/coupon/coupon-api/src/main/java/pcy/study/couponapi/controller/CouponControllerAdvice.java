package pcy.study.couponapi.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pcy.study.couponapi.controller.dto.CouponIssueResponse;
import pcy.study.couponcore.exception.CouponIssueException;

@RestControllerAdvice
public class CouponControllerAdvice {

    @ExceptionHandler(CouponIssueException.class)
    public CouponIssueResponse couponIssueExceptionHandler(CouponIssueException exception) {
        return new CouponIssueResponse(false, exception.getErrorCode().getMessage());
    }
}
