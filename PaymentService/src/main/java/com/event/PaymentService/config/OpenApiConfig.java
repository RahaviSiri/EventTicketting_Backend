package com.event.PaymentService.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Payment Service API")
                .description("Event Platform Payment microservice")
                .version("v1.0.0")
                .license(new License().name("Apache 2.0").url("https://springdoc.org")))
            .externalDocs(new ExternalDocumentation()
                .description("Project README")
                .url("https://example.com"));
    }
}


