package cn.com.traninfo.fastlcdp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger配置类
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FastLCDP API文档")
                        .description("基于XML定义快速生成数据库表的低代码开发平台API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FastLCDP Team")
                                .email("support@traninfo.com.cn")
                                .url("https://github.com/traninfo/fastlcdp"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("开发环境"),
                        new Server().url("https://api.fastlcdp.com").description("生产环境")
                ));
    }
}