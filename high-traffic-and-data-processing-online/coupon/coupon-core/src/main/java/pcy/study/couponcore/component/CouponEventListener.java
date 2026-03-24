package pcy.study.couponcore.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pcy.study.couponcore.model.event.CouponIssueCompleteEvent;
import pcy.study.couponcore.service.CouponCacheService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CouponEventListener {

    private final CouponCacheService couponCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void issueComplete(CouponIssueCompleteEvent event) {
        log.info("Issue complete. Cache refresh start couponId: {}", event.couponId());
        couponCacheService.putCouponCache(event.couponId());
        couponCacheService.putCouponLocalCache(event.couponId());
        log.info("Issue complete. Cache refresh end couponId: {}", event.couponId());
    }
}
