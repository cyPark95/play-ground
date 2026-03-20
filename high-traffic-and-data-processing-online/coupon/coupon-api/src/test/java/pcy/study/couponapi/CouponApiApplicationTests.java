package pcy.study.couponapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.config.name=application-core")
class CouponApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
