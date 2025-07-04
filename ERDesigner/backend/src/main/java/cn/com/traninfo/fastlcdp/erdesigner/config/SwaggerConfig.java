package cn.com.traninfo.fastlcdp.erdesigner.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import cn.com.traninfo.fastlcdp.erdesigner.util.MessageUtils;

/**
 * Swagger Configuration Class
 */
@Configuration
public class SwaggerConfig {
    
    @Autowired
    private MessageUtils messageUtils;
    
    @Bean
    public OpenAPI customOpenAPI() {
        String tagName = messageUtils.getMessage("api.database.management.tag.name");
        String tagDesc = messageUtils.getMessage("api.database.management.tag.description");
        return new OpenAPI()
                .addTagsItem(new Tag().name(tagName).description(tagDesc))
                .info(new Info()
                        .title(messageUtils.getMessage("config.fastlcdp.api.doc"))
                        .description(messageUtils.getMessage("config.api.description"))
                        .version("1.0.0")
                        .contact(new Contact()
                                .name(messageUtils.getMessage("config.api.contact.name"))
                                .email(messageUtils.getMessage("config.api.contact.email"))
                                .url(messageUtils.getMessage("config.api.contact.url")))
                        .license(new License()
                                .name(messageUtils.getMessage("config.api.license.name"))
                                .url(messageUtils.getMessage("config.api.license.url"))));
    }
}