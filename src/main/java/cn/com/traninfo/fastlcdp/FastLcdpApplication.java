package cn.com.traninfo.fastlcdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * FastLCDP应用程序启动类
 */
@SpringBootApplication
@EntityScan(basePackages = "cn.com.traninfo.fastlcdp.model")
@EnableJpaRepositories(basePackages = "cn.com.traninfo.fastlcdp.repository")
public class FastLcdpApplication {
    public static void main(String[] args) {
        SpringApplication.run(FastLcdpApplication.class, args);
    }
}