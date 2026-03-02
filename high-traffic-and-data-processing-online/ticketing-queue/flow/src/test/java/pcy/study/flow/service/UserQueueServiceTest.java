package pcy.study.flow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import pcy.study.flow.EmbeddedRedis;
import pcy.study.flow.exception.ApplicationException;
import reactor.test.StepVerifier;

@SpringBootTest
@Import(EmbeddedRedis.class)
@ActiveProfiles("test")
class UserQueueServiceTest {

    @Autowired
    private UserQueueService userQueueService;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private static final String QUEUE = "default";
    private static final Long USER_ID_1 = 100L;
    private static final Long USER_ID_2 = 101L;
    private static final Long USER_ID_3 = 102L;

    @BeforeEach
    public void beforeEach() {
        reactiveRedisTemplate.getConnectionFactory()
                .getReactiveConnection()
                .serverCommands()
                .flushAll()
                .block();
    }

    @Test
    @DisplayName("대기열에 유저를 정상적으로 등록하고 순위를 반환한다")
    void registerWaitQueue_NewUser_ReturnsRank() {
        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_1))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_2))
                .expectNext(2L)
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_3))
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    @DisplayName("이미 대기열에 존재하는 유저가 다시 등록을 시도하면 예외가 발생한다")
    void registerWaitQueue_AlreadyRegisteredUser_ThrowsException() {
        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_1))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_1))
                .expectError(ApplicationException.class)
                .verify();
    }

    @Test
    @DisplayName("대기열이 비어있을 때 허용 로직을 실행하면 0을 반환한다")
    void allowUser_EmptyWaitQueue_ReturnsZero() {
        StepVerifier.create(userQueueService.allowUser(QUEUE, 3L))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("대기열의 유저들을 지정한 수만큼 진행열로 이동시키고, 실제 상태를 검증한다")
    void allowUser_ValidCount_MovesUsersToProceedQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_1)
                        .then(userQueueService.registerWaitQueue(QUEUE, USER_ID_2))
                        .then(userQueueService.registerWaitQueue(QUEUE, USER_ID_3))
                        .then(userQueueService.allowUser(QUEUE, 2L)))
                .expectNext(2L)
                .verifyComplete();

        StepVerifier.create(userQueueService.isAllowed(QUEUE, USER_ID_1)).expectNext(true).verifyComplete();
        StepVerifier.create(userQueueService.isAllowed(QUEUE, USER_ID_2)).expectNext(true).verifyComplete();
        StepVerifier.create(userQueueService.isAllowed(QUEUE, USER_ID_3)).expectNext(false).verifyComplete();
    }

    @Test
    @DisplayName("요청한 허용 수보다 대기열 유저가 적을 경우, 존재하는 유저만큼만 이동시킨다")
    void allowUser_CountExceedsWaitingUsers_MovesOnlyExistingUsers() {
        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_1)
                        .then(userQueueService.registerWaitQueue(QUEUE, USER_ID_2))
                        .then(userQueueService.registerWaitQueue(QUEUE, USER_ID_3))
                        .then(userQueueService.allowUser(QUEUE, 5L)))
                .expectNext(3L)
                .verifyComplete();

        StepVerifier.create(userQueueService.isAllowed(QUEUE, USER_ID_3)).expectNext(true).verifyComplete();
    }

    @Test
    @DisplayName("허용 로직 실행 후 새로운 유저가 대기열에 등록되면 정상적인 순위를 받는다")
    void registerWaitQueue_AfterQueueCleared_StartsRankFromOne() {
        Long newUser = 200L;

        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_1)
                        .then(userQueueService.registerWaitQueue(QUEUE, USER_ID_2))
                        .then(userQueueService.registerWaitQueue(QUEUE, USER_ID_3))
                        .then(userQueueService.allowUser(QUEUE, 3L))
                        .then(userQueueService.registerWaitQueue(QUEUE, newUser))
                )
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    @DisplayName("대기열에 아예 등록되지 않은 유저는 진행열 진입이 불가능하다")
    void isAllowed_UnregisteredUser_ReturnsFalse() {
        StepVerifier.create(userQueueService.isAllowed(QUEUE, USER_ID_1))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("대기열에 있지만 아직 허용(allowUser)되지 않은 유저는 진행열 진입이 불가능하다")
    void isAllowed_UserStillInWaitQueue_ReturnsFalse() {
        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_1)
                        .then(userQueueService.allowUser(QUEUE, 3L))
                        .then(userQueueService.isAllowed(QUEUE, USER_ID_2)))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("진행열로 이동된 유저는 진입이 가능(true)하다")
    void isAllowed_UserInProceedQueue_ReturnsTrue() {
        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_1)
                        .then(userQueueService.allowUser(QUEUE, 3L))
                        .then(userQueueService.isAllowed(QUEUE, USER_ID_1)))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("대기열에 있는 유저의 순위를 조회하면 1부터 시작하는 올바른 대기 순위를 반환한다")
    void getRank_UserInWaitQueue_ReturnsRank() {
        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_1)
                        .then(userQueueService.getRank(QUEUE, USER_ID_1)))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(userQueueService.registerWaitQueue(QUEUE, USER_ID_2)
                        .then(userQueueService.getRank(QUEUE, USER_ID_2)))
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    @DisplayName("대기열에 존재하지 않는 유저의 순위를 조회하면 -1을 반환한다")
    void getRank_UserNotInWaitQueue_ReturnsMinusOne() {
        StepVerifier.create(userQueueService.getRank(QUEUE, USER_ID_1))
                .expectNext(-1L)
                .verifyComplete();
    }
}
