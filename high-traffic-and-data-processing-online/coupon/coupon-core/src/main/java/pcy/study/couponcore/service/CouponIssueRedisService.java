package pcy.study.couponcore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pcy.study.couponcore.repository.redis.RedisRepository;

import static pcy.study.couponcore.util.CouponRedisUtils.getIssueRequestKey;

@Service
@RequiredArgsConstructor
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;

    public boolean availableUserIssueQuantity(long couponId, long userId) {
        String key = getIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }

    public boolean availableTotalIssueQuantity(Integer totalQuantity, long couponId) {
        if (totalQuantity == null) {
            return true;
        }

        String key = getIssueRequestKey(couponId);
        return totalQuantity > redisRepository.sCard(key);
    }
}
