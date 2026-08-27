package io.github.kpsantiago.caca_oinbu.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openApi() {
        String securitySchemeName = "bearerAuth";
        var securityScheme = new SecurityScheme();
        securityScheme
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .name(securitySchemeName)
                .addExtension("x-jwt-token", true);

        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Caca Oinbu API")
                        .version("1.0")
                        .description("API para o Caça Oinbu"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, securityScheme));
    }
}
