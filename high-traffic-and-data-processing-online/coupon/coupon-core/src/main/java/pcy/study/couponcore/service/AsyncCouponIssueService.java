package pcy.study.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pcy.study.couponcore.component.DistributeLockExecutor;
import pcy.study.couponcore.exception.CouponIssueException;
import pcy.study.couponcore.exception.ErrorCode;
import pcy.study.couponcore.repository.redis.RedisRepository;
import pcy.study.couponcore.repository.redis.dto.CouponIssueRequest;
import pcy.study.couponcore.repository.redis.dto.CouponRedisEntity;

import static pcy.study.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static pcy.study.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@Service
@RequiredArgsConstructor
public class AsyncCouponIssueService {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final CouponCacheService couponCacheService;
    private final DistributeLockExecutor distributeLockExecutor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId) {
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuableCoupon();

        /*
         * 문제점
         * 처리량(Throughput) 감소 - 분산 락 획득/해제 과정 자체가 병목
         */
        distributeLockExecutor.execute(
                "lock_%s".formatted(couponId),
                3000,
                5000,
                () -> {
                    couponIssueRedisService.checkCouponIssueQuantity(coupon, userId);
                    issueRequest(couponId, userId);
                }
        );
    }

    private void issueRequest(long couponId, long userId) {
        var issueRequest = new CouponIssueRequest(couponId, userId);
        try {
            String value = objectMapper.writeValueAsString(issueRequest);
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
            redisRepository.rPush(getIssueRequestQueueKey(), value);
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(
                    ErrorCode.FAIL_COUPON_ISSUE_REQUEST,
                    String.format("input: %s", issueRequest)
            );
        }
    }
}
