package cn.com.traninfo.fastlcdp.erdesigner.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import cn.com.traninfo.fastlcdp.erdesigner.util.MessageUtils;

@Slf4j
@Component
public class ApiDocStartupLogger implements CommandLineRunner {

    @Value("${server.port:8080}")
    private String port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${springdoc.api-docs.path:/api-docs}")
    private String apiDocsPath;

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerUiPath;

    @Override
    public void run(String... args) {
        String base = "http://localhost:" + port + (contextPath.endsWith("/") ? contextPath : contextPath + "/");
        log.info("----------------------------------------------------------");
        log.info("API Documentation URL:" + base + apiDocsPath.replaceFirst("^/", ""));
        log.info("Swagger UI URL:" + base + swaggerUiPath.replaceFirst("^/", ""));
        log.info("----------------------------------------------------------\n");
    }
}
