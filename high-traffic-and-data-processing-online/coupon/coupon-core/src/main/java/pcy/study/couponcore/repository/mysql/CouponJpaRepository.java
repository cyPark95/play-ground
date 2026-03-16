package pcy.study.couponcore.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import pcy.study.couponcore.model.Coupon;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
