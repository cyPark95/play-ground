package pcy.study.couponcore.repository.mysql;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pcy.study.couponcore.model.CouponIssue;

import static pcy.study.couponcore.model.QCouponIssue.couponIssue;

@Repository
@RequiredArgsConstructor
public class CouponIssueRepository {

    private final JPAQueryFactory queryFactory;

    public CouponIssue findFirsCouponIssue(long couponId, long userId) {
        return queryFactory.select(couponIssue)
                .from(couponIssue)
                .where(couponIssue.couponId.eq(couponId))
                .where(couponIssue.userId.eq(userId))
                .fetchFirst();
    }
}
