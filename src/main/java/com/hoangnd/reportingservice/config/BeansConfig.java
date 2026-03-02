package com.hoangnd.reportingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hoangnd.reportingservice.common.AppConstants.API_BASE;

@Configuration
public class BeansConfig {
    @Bean
    public CorsFilter corsFilter(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("https://estate.maik.io.vn",
                "http://localhost:5173"));
        config.setAllowedHeaders(Arrays.asList(
                        HttpHeaders.ORIGIN,
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaders.ACCEPT,
                        HttpHeaders.AUTHORIZATION,
                        "x-user-id"
                )
        );
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH"
        ));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
