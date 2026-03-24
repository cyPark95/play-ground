package pcy.study.couponcore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pcy.study.couponcore.repository.redis.RedisRepository;
import pcy.study.couponcore.repository.redis.dto.CouponRedisEntity;

@Service
@RequiredArgsConstructor
public class AsyncCouponIssueService {

    private final RedisRepository redisRepository;
    private final CouponCacheService couponCacheService;

    public void issue(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponLocalCache(couponId);
        coupon.checkIssuableCoupon();
        issueRequest(couponId, userId, coupon.totalQuantity());
    }

    private void issueRequest(long couponId, long userId, Integer totalIssueQuantity) {
        totalIssueQuantity = totalIssueQuantity == null ? Integer.MAX_VALUE : totalIssueQuantity;
        redisRepository.issueRequest(couponId, userId, totalIssueQuantity);
    }
}
