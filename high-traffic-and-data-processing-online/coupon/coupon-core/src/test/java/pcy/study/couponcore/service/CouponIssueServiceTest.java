package pcy.study.couponcore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pcy.study.couponcore.CouponCoreConfiguration;
import pcy.study.couponcore.exception.CouponIssueException;
import pcy.study.couponcore.exception.ErrorCode;
import pcy.study.couponcore.model.Coupon;
import pcy.study.couponcore.model.CouponIssue;
import pcy.study.couponcore.model.CouponType;
import pcy.study.couponcore.repository.mysql.CouponIssueJpaRepository;
import pcy.study.couponcore.repository.mysql.CouponIssueRepository;
import pcy.study.couponcore.repository.mysql.CouponJpaRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = CouponCoreConfiguration.class)
class CouponIssueServiceTest {

    @Autowired
    private CouponIssueService sut;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clean() {
        couponJpaRepository.deleteAllInBatch();
        couponIssueJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("쿠폰 발급 내역이 존재하면 예외를 반환한다")
    void saveCouponIssue_1() {
        // given
        CouponIssue issue = CouponIssue.builder()
                .couponId(1L)
                .userId(1L)
                .build();
        couponIssueJpaRepository.save(issue);

        // when & then
        assertThatThrownBy(() -> sut.saveCouponIssue(issue.getCouponId(), issue.getUserId()))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.DUPLICATE_COUPON_ISSUE);
    }

    @Test
    @DisplayName("쿠폰 발급 내역이 존재하지 않는다면 쿠폰을 발급한다.")
    void saveCouponIssue_2() {
        // given
        long couponId = 1L;
        long userId = 1L;

        // when
        CouponIssue result = sut.saveCouponIssue(couponId, userId);

        // then
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("발급 수량, 기한, 중복 발급 문제가 없다면 쿠폰을 발급한다")
    void issue_1() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when
        sut.issue(coupon.getId(), userId);

        // then
        Coupon couponResult = couponJpaRepository.findById(coupon.getId()).orElseThrow();
        assertThat(couponResult.getIssuedQuantity()).isEqualTo(1);

        CouponIssue couponIssueResult = couponIssueRepository.findFirsCouponIssue(coupon.getId(), userId);
        assertThat(couponIssueResult).isNotNull();
    }

    @Test
    @DisplayName("발급 수량에 문제가 있다면 예외를 반환한다")
    void issue_2() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when & then
        assertThatThrownBy(() -> sut.issue(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);
    }

    @Test
    @DisplayName("발급 기한에 문제가 있다면 예외를 반환한다")
    void issue_3() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now())
                .build();
        couponJpaRepository.save(coupon);

        // when & then
        assertThatThrownBy(() -> sut.issue(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @DisplayName("중복 발급 검증에 문제가 있다면 예외를 반환한다")
    void issue_4() {
        // given
        long userId = 1L;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        CouponIssue issue = CouponIssue.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .build();
        couponIssueJpaRepository.save(issue);

        // when & then
        assertThatThrownBy(() -> sut.issue(coupon.getId(), userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.DUPLICATE_COUPON_ISSUE);
    }

    @Test
    @DisplayName("쿠폰이 존재하지 않는다면 예외를 반환한다")
    void issue_5() {
        // given
        long couponId = 1L;
        long userId = 1L;

        // when & then
        assertThatThrownBy(() -> sut.issue(couponId, userId))
                .isInstanceOf(CouponIssueException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.COUPON_NOT_EXIST);
    }
}
