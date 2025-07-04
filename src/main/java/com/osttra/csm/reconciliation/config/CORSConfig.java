package com.osttra.csm.reconciliation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CORSConfig {

    @Value("${web.cors.allowed-origins}")
    private List<String> corsUrlList;

    @Value("${web.cors.allowed-methods}")
    private List<String> corsMethodList;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.setAllowedOrigins(corsUrlList);
        config.setAllowedMethods(corsMethodList);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}