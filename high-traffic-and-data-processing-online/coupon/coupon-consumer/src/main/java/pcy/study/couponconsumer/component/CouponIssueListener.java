package pcy.study.couponconsumer.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pcy.study.couponcore.repository.redis.RedisRepository;
import pcy.study.couponcore.repository.redis.dto.CouponIssueRequest;
import pcy.study.couponcore.service.CouponIssueService;
import pcy.study.couponcore.util.CouponRedisUtils;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class CouponIssueListener {

    private static final String ISSUE_REQUEST_QUEUE_KEY = CouponRedisUtils.getIssueRequestQueueKey();

    private final RedisRepository redisRepository;
    private final CouponIssueService couponIssueService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedDelay = 1000L)
    public void issue() throws JsonProcessingException {
        log.info("listen...");
        while (existCouponIssueTarget()) {
            CouponIssueRequest target = getIssueTarget();
            log.info("발급 시작 target: {}", target);

            couponIssueService.issue(target.couponId(), target.userId());
            log.info("발급 완료 target: {}", target);

            removeIssueTarget();
        }
    }

    private boolean existCouponIssueTarget() {
        return redisRepository.lSize(ISSUE_REQUEST_QUEUE_KEY) > 0;
    }

    private CouponIssueRequest getIssueTarget() throws JsonProcessingException {
        String issueRequest = redisRepository.lIndex(ISSUE_REQUEST_QUEUE_KEY, 0);
        return objectMapper.readValue(issueRequest, CouponIssueRequest.class);
    }

    private void removeIssueTarget() {
        redisRepository.lPop(ISSUE_REQUEST_QUEUE_KEY);
    }
}
