package pcy.study.couponcore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcy.study.couponcore.exception.CouponIssueException;
import pcy.study.couponcore.exception.ErrorCode;
import pcy.study.couponcore.model.Coupon;
import pcy.study.couponcore.model.CouponIssue;
import pcy.study.couponcore.model.event.CouponIssueCompleteEvent;
import pcy.study.couponcore.repository.mysql.CouponIssueJpaRepository;
import pcy.study.couponcore.repository.mysql.CouponIssueRepository;
import pcy.study.couponcore.repository.mysql.CouponJpaRepository;

@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void issue(long couponId, long userId) {
        Coupon coupon = findCouponWithLock(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
        publishCouponEvent(coupon);
    }

    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponJpaRepository.findById(couponId)
                .orElseThrow(() -> new CouponIssueException(
                        ErrorCode.COUPON_NOT_EXIST,
                        String.format("쿠폰 정책이 존재하지 않습니다. %s", couponId))
                );
    }

    @Transactional(readOnly = true)
    public Coupon findCouponWithLock(long couponId) {
        return couponJpaRepository.findCouponWithLock(couponId)
                .orElseThrow(() -> new CouponIssueException(
                        ErrorCode.COUPON_NOT_EXIST,
                        String.format("쿠폰 정책이 존재하지 않습니다. %s", couponId))
                );
    }

    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId) {
        checkAlreadyIssuance(couponId, userId);
        CouponIssue issue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        return couponIssueJpaRepository.save(issue);
    }

    private void checkAlreadyIssuance(long couponId, long userId) {
        CouponIssue issue = couponIssueRepository.findFirsCouponIssue(couponId, userId);
        if (issue != null) {
            throw new CouponIssueException(
                    ErrorCode.DUPLICATE_COUPON_ISSUE,
                    String.format("이미 발급된 쿠폰입니다. user_id: %s, coupon_id: %s", userId, couponId)
            );
        }
    }

    private void publishCouponEvent(Coupon coupon) {
        if (coupon.isIssueComplete()) {
            CouponIssueCompleteEvent event = new CouponIssueCompleteEvent(coupon.getId());
            applicationEventPublisher.publishEvent(event);
        }
    }
}
