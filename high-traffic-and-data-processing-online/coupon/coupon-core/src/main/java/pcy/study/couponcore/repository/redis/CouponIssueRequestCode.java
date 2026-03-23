package pcy.study.couponcore.repository.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pcy.study.couponcore.exception.CouponIssueException;
import pcy.study.couponcore.exception.ErrorCode;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum CouponIssueRequestCode {

    SUCCESS(1) {
        @Override
        public void validate() {
        }
    },

    DUPLICATED_COUPON_ISSUE(2) {
        @Override
        public void validate() {
            throw new CouponIssueException(ErrorCode.DUPLICATE_COUPON_ISSUE, "이미 발급된 쿠폰입니다.");
        }
    },

    INVALID_COUPON_ISSUE_QUANTITY(3) {
        @Override
        public void validate() {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "발급 가능한 수량을 초과합니다.");
        }
    };
    ;

    private final int code;

    public static CouponIssueRequestCode find(String code) {
        int codeValue = Integer.parseInt(code);
        return Stream.of(CouponIssueRequestCode.values())
                .filter(value -> value.getCode() == codeValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드 입니다."));
    }

    public abstract void validate();
}
