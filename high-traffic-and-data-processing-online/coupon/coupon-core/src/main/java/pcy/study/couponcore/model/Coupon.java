package pcy.study.couponcore.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcy.study.couponcore.exception.CouponIssueException;
import pcy.study.couponcore.exception.ErrorCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minAvailableAmount;

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;

    @Builder
    public Coupon(String title, CouponType couponType, Integer totalQuantity, int issuedQuantity, int discountAmount, int minAvailableAmount, LocalDateTime dateIssueStart, LocalDateTime dateIssueEnd) {
        this.title = title;
        this.couponType = couponType;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = issuedQuantity;
        this.discountAmount = discountAmount;
        this.minAvailableAmount = minAvailableAmount;
        this.dateIssueStart = dateIssueStart;
        this.dateIssueEnd = dateIssueEnd;
    }

    public boolean availableIssueQuantity() {
        if (totalQuantity == null) {
            return true;
        }

        return totalQuantity > issuedQuantity;
    }

    public boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }

    public boolean isIssueComplete() {
        LocalDateTime now = LocalDateTime.now();
        return dateIssueEnd.isBefore(now) || !availableIssueQuantity();
    }

    public void issue() {
        if (!availableIssueQuantity()) {
            throw new CouponIssueException(
                    ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                    String.format("발금 가능 수량을 초과합니다. total: %s, issued: %s", totalQuantity, issuedQuantity)
            );
        }

        if (!availableIssueDate()) {
            throw new CouponIssueException(
                    ErrorCode.INVALID_COUPON_ISSUE_DATE,
                    String.format("발급 가능한 일자가 아닙니다. request: %s, issueStart: %s, issueEnd: %s", LocalDateTime.now(), dateIssueStart, dateIssueEnd)
            );
        }

        issuedQuantity++;
    }
}
