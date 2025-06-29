package cn.com.traninfo.fastlcdp.erdesigner.config;

import cn.com.traninfo.fastlcdp.erdesigner.util.MessageUtils;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger Configuration Class
 */
@Configuration
public class SwaggerConfig {
    
    @Autowired
    private MessageUtils messageUtils;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(messageUtils.getMessage("config.fastlcdp.api.doc"))
                        .description(messageUtils.getMessage("config.api.description"))
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FastLCDP Team")
                                .email("shijiachen@traninfo.com.cn")
                                .url("https://github.com/traninfo/fastlcdp"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description(messageUtils.getMessage("config.dev.environment")),
                        new Server().url("https://api.traninfo.com.cn").description(messageUtils.getMessage("config.prod.environment"))
                ));
    }
}