package pcy.study.couponcore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@ComponentScan
@EnableAutoConfiguration
@EnableJpaAuditing
@EnableCaching
public class CouponCoreConfiguration {
}
