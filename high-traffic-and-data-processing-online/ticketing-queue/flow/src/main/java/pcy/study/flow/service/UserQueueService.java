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
}
