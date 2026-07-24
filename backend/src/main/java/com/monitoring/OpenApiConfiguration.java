package com.monitoring;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .servers(List.of(
                new Server().url("http://localhost:8080/api").description("Local development"),
                new Server().url("/api").description("Relative path")
            ))
            .info(new Info()
                .title("Incident Monitoring API")
                .version("1.0.0")
                .description(
                    "Application Monitoring & Incident Investigation System\n\n" +
                    "This API provides:\n" +
                    "- **Incident Management**: Track and manage application incidents\n" +
                    "- **Severity Filtering**: Filter incidents by severity level\n" +
                    "- **Status Tracking**: Monitor incident status (Open, Investigating, Resolved)\n" +
                    "- **Application Monitoring**: Track incidents by application\n" +
                    "- **Email Alerts**: Process APP SUPPORT email notifications\n\n" +
                    "All endpoints use the /api context path"
                )
                .contact(new Contact()
                    .name("RAG Agent Support")
                    .url("https://github.com/example/rag-agent")
                    .email("support@example.com")
                )
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")
                )
            );
    }
}
