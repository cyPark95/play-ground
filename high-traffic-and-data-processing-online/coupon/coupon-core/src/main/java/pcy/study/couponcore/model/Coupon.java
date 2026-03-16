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
}
