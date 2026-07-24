package com.oss.oss_vulnerability_fix.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OSS Vulnerability Fix API")
                        .description("API for scanning application dependencies against the OSV vulnerability database")
                        .version("1.0.0"));
    }
}
