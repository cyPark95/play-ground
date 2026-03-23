package pcy.study.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import pcy.study.couponcore.CouponCoreConfiguration;
import pcy.study.couponcore.exception.CouponIssueException;
import pcy.study.couponcore.exception.ErrorCode;
import pcy.study.couponcore.model.Coupon;
import pcy.study.couponcore.model.CouponType;
import pcy.study.couponcore.repository.mysql.CouponJpaRepository;
import pcy.study.couponcore.repository.redis.dto.CouponIssueRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pcy.study.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static pcy.study.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@SpringBootTest(classes = CouponCoreConfiguration.class)
class AsyncCouponIssueServiceTest {

    @Autowired
    private AsyncCouponIssueService sut;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않는다면 예외를 반환한다")
    void issue_1() {
        // given
        long couponId = 1L;
        long userId = 1L;

        // when & then
        assertThatThrownBy(() -> sut.issue(couponId, userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.COUPON_NOT_EXIST);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 가능 수량이 존재하지 않는다면 예외를 반환한다")
    void issue_2() {
        // given
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        LongStream.range(0, coupon.getTotalQuantity()).forEach(index -> redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(index)));

        long userId = 9999;

        // when & then
        assertThatThrownBy(() -> sut.issue(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);
    }

    @Test
    @DisplayName("쿠폰 발급 - 이미 발급된 사용자라면 예외를 반환한다")
    void issue_3() {
        // given
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        long userId = 1;
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(userId));

        // when & then
        assertThatThrownBy(() -> sut.issue(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.DUPLICATE_COUPON_ISSUE);
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 기한이 유효하지 않다면 예외를 반환한다")
    void issue_4() {
        // given
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now())
                .build();
        couponJpaRepository.save(coupon);

        long userId = 1;

        // when & then
        assertThatThrownBy(() -> sut.issue(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급을 기록한다")
    void issue_5() {
        // given
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        long userId = 1;

        // when
        sut.issue(coupon.getId(), userId);

        // then
        Boolean isSaved = redisTemplate.opsForSet().isMember(getIssueRequestKey(coupon.getId()), String.valueOf(userId));
        assertThat(isSaved).isTrue();
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급 요청이 성공하면 쿠폰 발급 큐에 적재된다")
    void issue_6() throws JsonProcessingException {
        // given
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        long userId = 1;
        CouponIssueRequest request = new CouponIssueRequest(coupon.getId(), userId);

        // when
        sut.issue(coupon.getId(), userId);

        // then
        String savedIssueRequest = redisTemplate.opsForList().leftPop(getIssueRequestQueueKey());
        assertThat(savedIssueRequest).isEqualTo(new ObjectMapper().writeValueAsString(request));
    }
}
