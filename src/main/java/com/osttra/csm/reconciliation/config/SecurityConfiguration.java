package com.osttra.csm.reconciliation.config;

import com.osttra.csm.reconciliation.util.JWTUtil;
import com.osttra.csm.cognito.CognitoJwtValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class
 */

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConfiguration {

    private final JWTUtil jwtUtil;

    private CognitoJwtValidator cognitoJwtValidator;

    public SecurityConfiguration(JWTUtil jwtUtil, CognitoJwtValidator cognitoJwtValidator) {

        this.jwtUtil = jwtUtil;
        this.cognitoJwtValidator = cognitoJwtValidator;
    }

    @Bean
    protected EnrichmentFilter enrichmentFilter() {
        return new EnrichmentFilter(this.jwtUtil, this.cognitoJwtValidator);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(enrichmentFilter(), UsernamePasswordAuthenticationFilter.class);
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry.anyRequest().permitAll());

        return http.build();
    }
}
