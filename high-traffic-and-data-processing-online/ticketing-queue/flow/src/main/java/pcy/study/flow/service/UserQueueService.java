package pcy.study.flow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import pcy.study.flow.exception.ErrorCode;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserQueueService {

    private static final String USER_QUEUE_WAIT_KEY = "users:queue:%s:wait";
    private static final String USER_QUEUE_PROCEED_KEY = "users:queue:%s:proceed";

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

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
}
