package br.gov.mg.bomdestino.coreservice.config;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelRouteConfig extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")

                //Enable swagger endpoint.
                .apiContextPath("/swagger") //swagger endpoint path
                .apiContextRouteId("swagger") //id of route providing the swagger endpoint

                //Swagger properties
                .apiProperty("base.path", "/camel")
                .apiProperty("api.title", "core-service")
                .apiProperty("api.description", "SGM - Módulo de Integração Geral")
                .apiProperty("api.version", "1.0")
                .apiProperty("api.contact.name", "Douglas Miranda")
                .apiProperty("api.contact.email", "douglasmacbrito@gmail.com")
                .apiProperty("api.contact.url", "https://github.com/")
                .apiProperty("host", "") //by default 0.0.0.0
                .apiProperty("port", "8080")
        ;
    }
}