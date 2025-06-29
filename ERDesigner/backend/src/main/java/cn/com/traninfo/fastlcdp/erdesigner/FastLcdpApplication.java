package cn.com.traninfo.fastlcdp.erdesigner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FastLCDP应用程序启动类
 */
@SpringBootApplication
@MapperScan("cn.com.traninfo.fastlcdp.erdesigner.entity")
public class FastLcdpApplication {
    public static void main(String[] args) {
        SpringApplication.run(FastLcdpApplication.class, args);
    }
}