package pcy.study.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pcy.study.couponcore.component.DistributeLockExecutor;
import pcy.study.couponcore.exception.CouponIssueException;
import pcy.study.couponcore.exception.ErrorCode;
import pcy.study.couponcore.model.Coupon;
import pcy.study.couponcore.repository.redis.RedisRepository;
import pcy.study.couponcore.repository.redis.dto.CouponIssueRequest;

import static pcy.study.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static pcy.study.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@Service
@RequiredArgsConstructor
public class AsyncCouponIssueService {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void issue(long couponId, long userId) {
        Coupon coupon = couponIssueService.findCoupon(couponId);

        if (!coupon.availableIssueDate()) {
            throw new CouponIssueException(
                    ErrorCode.INVALID_COUPON_ISSUE_DATE,
                    String.format("발급 가능 일자가 아닙니다. couponId: %d, issueStart: %s, issueEnd: %s", couponId, coupon.getDateIssueStart(), coupon.getDateIssueEnd())
            );
        }

        distributeLockExecutor.execute(
                "lock_%s".formatted(couponId),
                3000,
                5000,
                () -> {
                    if (!couponIssueRedisService.availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId)) {
                        throw new CouponIssueException(
                                ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                                String.format("발급 가능한 수량을 초과합니다. couponId: %d, userId: %d", couponId, userId)
                        );
                    }

                    if (!couponIssueRedisService.availableUserIssueQuantity(coupon.getId(), userId)) {
                        throw new CouponIssueException(
                                ErrorCode.DUPLICATE_COUPON_ISSUE,
                                String.format("이미 발급 요청이 처리 됐습니다. couponId: %d, userId: %d", couponId, userId)
                        );
                    }

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
