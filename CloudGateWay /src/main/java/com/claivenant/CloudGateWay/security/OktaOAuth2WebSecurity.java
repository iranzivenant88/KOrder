package com.claivenant.CloudGateWay.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFluxSecurity
public class OktaOAuth2WebSecurity {

    @Bean
    public SecurityWebFilterChain  securityWebFilterChain(ServerHttpSecurity http){
        http
                .authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .oauth2Login()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();


    }

}
