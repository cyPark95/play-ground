package pcy.study.couponcore.repository.redis.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import pcy.study.couponcore.exception.CouponIssueException;
import pcy.study.couponcore.exception.ErrorCode;
import pcy.study.couponcore.model.Coupon;
import pcy.study.couponcore.model.CouponType;

import java.time.LocalDateTime;

public record CouponRedisEntity(
        Long id,
        CouponType couponType,
        Integer totalQuantity,
        boolean availableIssueQuantity,

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime dateIssueStart,

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime dateIssueEnd
) {

    public CouponRedisEntity(Coupon coupon) {
        this(
                coupon.getId(),
                coupon.getCouponType(),
                coupon.getTotalQuantity(),
                coupon.availableIssueQuantity(),
                coupon.getDateIssueStart(),
                coupon.getDateIssueEnd()
        );
    }

    public void checkIssuableCoupon() {
        if(!availableIssueQuantity) {
            throw new CouponIssueException(
                    ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                    String.format("발급 가능한 수량을 초과합니다. couponId: %d", id)
            );
        }

        if(!availableIssueDate()) {
            throw new CouponIssueException(
                    ErrorCode.INVALID_COUPON_ISSUE_DATE,
                    String.format("발급 가능 일자가 아닙니다. couponId: %d, issueStart: %s, issueEnd: %s", id, dateIssueStart(), dateIssueEnd())
            );
        }
    }

    private boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }
}
