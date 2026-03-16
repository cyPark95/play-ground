CREATE TABLE `coupons`
(
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `title`                VARCHAR(255) NOT NULL COMMENT '쿠폰명',
    `coupon_type`          VARCHAR(255) NOT NULL COMMENT '쿠폰 타입 (선착순 쿠폰, ..)',
    `total_quantity`       INT COMMENT '쿠폰 발급 최대 수량',
    `issued_quantity`      INT          NOT NULL COMMENT '발급된 쿠폰 수량',
    `discount_amount`      INT          NOT NULL COMMENT '할인 금액',
    `min_available_amount` INT          NOT NULL COMMENT '최소 사용 금액',
    `date_issue_start`     DATETIME(6)  NOT NULL COMMENT '발급 시작 일시',
    `date_issue_end`       DATETIME(6)  NOT NULL COMMENT '발급 종료 일시',
    `date_created`         DATETIME(6)  NOT NULL COMMENT '생성 일시',
    `date_updated`         DATETIME(6)  NOT NULL COMMENT '수정 일시'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='쿠폰 정책';

CREATE TABLE `coupon_issues`
(
    `id`           BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `coupon_id`    BIGINT      NOT NULL COMMENT '쿠폰 ID',
    `user_id`      BIGINT      NOT NULL COMMENT '유저 ID',
    `date_issued`  DATETIME(6) NOT NULL COMMENT '발급 일시',
    `date_used`    DATETIME(6) NULL COMMENT '사용 일시',
    `date_created` DATETIME(6) NOT NULL COMMENT '생성 일시',
    `date_updated` DATETIME(6) NOT NULL COMMENT '수정 일시',
    CONSTRAINT `fk_coupon_issues_coupon_id` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='쿠폰 발급 내역';
