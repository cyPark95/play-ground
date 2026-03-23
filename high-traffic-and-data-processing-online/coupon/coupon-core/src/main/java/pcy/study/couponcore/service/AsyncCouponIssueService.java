package pcy.study.couponcore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pcy.study.couponcore.repository.redis.RedisRepository;

@Service
@RequiredArgsConstructor
public class AsyncCouponIssueService {

    private final RedisRepository redisRepository;

    /*
     * 문제점
     * 1. Score가 같은 경우, Value 사전 순 정렬(정확한 순서 판단 X)
     * 2. 데이터 추가 개수 제한 X - ZADD 시간 복잡도 O(log N)
     */
    public void issue(long couponId, long userId) {
        String key = String.format("issue:request:sorted_set:couponId:%s", couponId);
        redisRepository.zAdd(key, String.valueOf(userId), System.currentTimeMillis());
    }
}
