package com.example.pizzumburgum.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "PizzUM & BurgUM API",
                version = "1.0"
        )
)
@SecurityScheme(
        name = "DGI-API-KEY",
        type = SecuritySchemeType.APIKEY,
        paramName = "X-API-Key",
        in = SecuritySchemeIn.HEADER
)
@SecurityScheme(
        name = "BPS-API-KEY",
        type = SecuritySchemeType.APIKEY,
        paramName = "X-API-Key",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
