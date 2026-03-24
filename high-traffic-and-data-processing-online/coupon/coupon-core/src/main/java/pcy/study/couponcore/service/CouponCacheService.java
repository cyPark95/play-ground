package pcy.study.couponcore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pcy.study.couponcore.model.Coupon;
import pcy.study.couponcore.repository.redis.dto.CouponRedisEntity;

@Service
@RequiredArgsConstructor
public class CouponCacheService {

    private final CouponIssueService couponIssueService;

    @Cacheable(cacheNames = "coupon")
    public CouponRedisEntity getCouponCache(long couponId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);
        return new CouponRedisEntity(coupon);
    }

    @Cacheable(cacheNames = "coupon", cacheManager = "localCacheManager")
    public CouponRedisEntity getCouponLocalCache(long couponId) {
        return proxy().getCouponCache(couponId);
    }

    private CouponCacheService proxy() {
        return (CouponCacheService) AopContext.currentProxy();
    }
}
