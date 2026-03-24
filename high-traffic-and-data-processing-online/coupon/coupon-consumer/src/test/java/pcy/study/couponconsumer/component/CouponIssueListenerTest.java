package pcy.study.couponconsumer.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pcy.study.couponcore.CouponCoreConfiguration;
import pcy.study.couponcore.repository.redis.RedisRepository;
import pcy.study.couponcore.service.CouponIssueService;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CouponCoreConfiguration.class)
@Import(CouponIssueListener.class)
class CouponIssueListenerTest {

    @Autowired
    private CouponIssueListener sut;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisRepository redisRepository;

    @MockitoBean
    private CouponIssueService couponIssueService;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 발급 Queue에 처리 대상이 없다면 발급을 하지 않는다")
    void issue_1() throws Exception {
        // when
        sut.issue();

        // then
        verify(couponIssueService, never()).issue(anyLong(), anyLong());
    }

    @Test
    @DisplayName("쿠폰 발급 Queue에 처리 대상이 있다면 발급한다")
    void issue_2() throws Exception {
        // given
        long couponId = 1L;
        long userId = 1L;
        int totalQuantity = Integer.MAX_VALUE;

        redisRepository.issueRequest(couponId, userId, totalQuantity);

        // when
        sut.issue();

        // then
        verify(couponIssueService, times(1)).issue(couponId, userId);
    }

    @Test
    @DisplayName("쿠폰 발급 요청 순서에 맞게 처리된다")
    void issue_3() throws Exception {
        // given
        long couponId = 1L;
        List<Long> userIds = List.of(1L, 2L, 3L);
        int totalQuantity = Integer.MAX_VALUE;

        userIds.forEach(userId -> redisRepository.issueRequest(couponId, userId, totalQuantity));

        // when
        sut.issue();

        // then
        InOrder inOrder = inOrder(couponIssueService);
        userIds.forEach(userId -> inOrder.verify(couponIssueService, times(1)).issue(couponId, userId));
    }
}
