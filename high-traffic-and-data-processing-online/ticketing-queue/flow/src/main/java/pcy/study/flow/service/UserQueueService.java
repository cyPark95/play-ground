package pcy.study.flow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pcy.study.flow.exception.ErrorCode;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserQueueService {

    private static final String USER_QUEUE_WAIT_KEY_FOR_SCAN = "users:queue:*:wait";
    private static final String USER_QUEUE_WAIT_KEY = "users:queue:%s:wait";
    private static final String USER_QUEUE_PROCEED_KEY = "users:queue:%s:proceed";

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Value("${scheduler.enabled}")
    private Boolean scheduling = false;

    public Mono<Long> registerWaitQueue(final String queue, final Long userId) {
        var key = USER_QUEUE_WAIT_KEY.formatted(queue);
        var user = userId.toString();
        long timestamp = Instant.now().toEpochMilli();

        return reactiveRedisTemplate.opsForZSet().rank(key, user)
                .flatMap(rank -> Mono.<Long>error(ErrorCode.QUEUE_ALREADY_REGISTERED_USER.build()))
                .switchIfEmpty(
                        reactiveRedisTemplate.opsForZSet().add(key, user, timestamp)
                                .flatMap(added -> reactiveRedisTemplate.opsForZSet().rank(key, user))
                )
                .map(rank -> rank + 1);
    }

    public Mono<Long> allowUser(final String queue, final Long count) {
        var waitKey = USER_QUEUE_WAIT_KEY.formatted(queue);
        var proceedKey = USER_QUEUE_PROCEED_KEY.formatted(queue);
        long timestamp = Instant.now().toEpochMilli();

        return reactiveRedisTemplate.opsForZSet().popMin(waitKey, count)
                .flatMap(user -> reactiveRedisTemplate.opsForZSet().add(proceedKey, user.getValue(), timestamp))
                .count();
    }

    public Mono<Boolean> isAllowed(final String queue, final Long userId) {
        var proceedKey = USER_QUEUE_PROCEED_KEY.formatted(queue);

        return reactiveRedisTemplate.opsForZSet().score(proceedKey, userId.toString())
                .hasElement();
    }

    public Mono<Long> getRank(final String queue, final Long userId) {
        var key = USER_QUEUE_WAIT_KEY.formatted(queue);
        var user = userId.toString();

        return reactiveRedisTemplate.opsForZSet().rank(key, user)
                .map(rank -> rank + 1)
                .defaultIfEmpty(-1L);
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 3000)
    public void scheduleAllowUser() {
        if (!scheduling) {
            log.info("passed scheduling...");
            return;
        }
        log.info("called scheduling...");

        var maxAllowUserCount = 3L;
        reactiveRedisTemplate.scan(ScanOptions.scanOptions()
                        .match(USER_QUEUE_WAIT_KEY_FOR_SCAN)
                        .count(100)
                        .build())
                .map(key -> key.split(":")[2])
                .flatMap(queue -> allowUser(queue, maxAllowUserCount)
                        .map(allowed -> Tuples.of(queue, allowed)), 10)
                .onErrorContinue((e, queue) -> log.error("Queue [{}] allowUser failed", queue, e))
                .doOnNext(tuple -> log.info("Tried {} and allowed {} members of {} queue", maxAllowUserCount, tuple.getT2(), tuple.getT1()))
                .blockLast();
    }
}
