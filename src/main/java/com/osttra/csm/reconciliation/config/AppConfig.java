package com.osttra.csm.reconciliation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // Marks this class as a source of bean definitions
public class AppConfig { // You can name this whatever makes sense, e.g., RestTemplateConfig

    @Bean // Marks the method's return value as a Spring bean
    public RestTemplate restTemplate() {
        // You can customize the RestTemplate here if needed (e.g., set timeouts, message converters)
        return new RestTemplate();
    }
}