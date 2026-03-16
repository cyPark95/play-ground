package pcy.study.couponconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pcy.study.couponcore.CouponCoreConfiguration;

@SpringBootApplication
@Import(CouponCoreConfiguration.class)
public class CouponConsumerApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application-core, application-consumer");
        SpringApplication.run(CouponConsumerApplication.class, args);
    }

}
