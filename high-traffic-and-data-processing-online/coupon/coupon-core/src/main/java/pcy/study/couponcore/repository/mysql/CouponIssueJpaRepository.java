package pcy.study.couponcore.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import pcy.study.couponcore.model.CouponIssue;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {
}
