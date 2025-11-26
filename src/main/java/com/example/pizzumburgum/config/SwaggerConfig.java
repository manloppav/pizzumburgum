package com.example.pizzumburgum.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PizzUM & BurgUM - API de Integración Externa")
                        .version("1.0.0")
                        .description("""
                                API REST para integración con organismos externos.
                                
                                ## Endpoints disponibles:
                                - **DGI**: Consulta de tickets de venta por fecha
                                - **BPS**: Consulta de cantidad de funcionarios del sistema
                                
                                ## Autenticación:
                                Todos los endpoints requieren autenticación mediante API Key en el header `X-API-Key`.
                                """)
                        .contact(new Contact()
                                .name("PizzUM & BurgUM - Equipo de Desarrollo")
                                .email("soporte@pizzumburgum.com")
                                .url("https://pizzumburgum.com"))
                        .license(new License()
                                .name("API License")
                                .url("https://pizzumburgum.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.pizzumburgum.com")
                                .description("Servidor de Producción")
                ));
    }
}
