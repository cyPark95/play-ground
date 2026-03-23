package pcy.study.couponcore.util;

public class CouponRedisUtils {

    public static String getIssueRequestKey(long couponId) {
        return String.format("issue:request:coupon:%s", couponId);
    }

    public static String getIssueRequestQueueKey() {
        return "issue:request";
    }
}
