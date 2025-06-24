package com.fastlcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * FastLCP应用程序启动类
 */
@SpringBootApplication
@EntityScan(basePackages = "com.fastlcp.model")
@EnableJpaRepositories(basePackages = "com.fastlcp.repository")
public class FastLcpApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FastLcpApplication.class, args);
    }
}