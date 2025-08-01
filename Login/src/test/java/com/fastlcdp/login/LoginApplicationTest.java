package com.fastlcdp.login;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * LoginApplication 测试类
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class LoginApplicationTest {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
    }

    @Test
    void applicationStarts() {
        // 测试应用是否能正常启动
        // 这个测试会验证所有的Bean是否能正确创建和注入
    }
}