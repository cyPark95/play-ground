package pcy.study.couponcore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pcy.study.couponcore.exception.CouponIssueException;
import pcy.study.couponcore.exception.ErrorCode;
import pcy.study.couponcore.repository.redis.RedisRepository;
import pcy.study.couponcore.repository.redis.dto.CouponRedisEntity;

import static pcy.study.couponcore.util.CouponRedisUtils.getIssueRequestKey;

@Service
@RequiredArgsConstructor
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;

    public void checkCouponIssueQuantity(CouponRedisEntity couponRedisEntity, long userId) {
        if (!availableTotalIssueQuantity(couponRedisEntity.totalQuantity(), couponRedisEntity.id())) {
            throw new CouponIssueException(
                    ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                    String.format("발급 가능한 수량을 초과합니다. couponId: %d, userId: %d", couponRedisEntity.id(), userId)
            );
        }

        if (!availableUserIssueQuantity(couponRedisEntity.id(), userId)) {
            throw new CouponIssueException(
                    ErrorCode.DUPLICATE_COUPON_ISSUE,
                    String.format("이미 발급 요청이 처리 됐습니다. couponId: %d, userId: %d", couponRedisEntity.id(), userId)
            );
        }
    }

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
