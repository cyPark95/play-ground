package pcy.study.couponcore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import pcy.study.couponcore.CouponCoreConfiguration;

import java.util.Collection;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static pcy.study.couponcore.util.CouponRedisUtils.getIssueRequestKey;

@SpringBootTest(classes = CouponCoreConfiguration.class)
class CouponIssueRedisServiceTest {

    @Autowired
    private CouponIssueRedisService sut;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 존재하면 true를 반환하다")
    void availableTotalIssueQuantity_1() {
        // given
        long couponId = 1;
        int totalIssueQuantity = 10;

        // when
        boolean result = sut.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 모두 소진되면 false를 반환하다")
    void availableTotalIssueQuantity_2() {
        // given
        long couponId = 1;
        int totalIssueQuantity = 10;

        LongStream.range(0, totalIssueQuantity)
                .forEach(userId -> redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId)));

        // when
        boolean result = sut.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 사용자가 존재하지 않으면 true를 반환한다")
    void availableUserIssueQuantity_1() {
        // given
        long couponId = 1;
        long userId = 1;

        // when
        boolean result = sut.availableUserIssueQuantity(couponId, userId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 사용자가 존재하면 false를 반환한다")
    void availableUserIssueQuantity_2() {
        // given
        long couponId = 1;
        long userId = 1;

        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));

        // when
        boolean result = sut.availableUserIssueQuantity(couponId, userId);

        // then
        assertThat(result).isFalse();
    }
}
