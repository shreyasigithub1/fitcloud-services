package com.fitcloud.member_service.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class MemberServiceSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

//        http
//                .cors(cors -> cors.configurationSource(request -> {
//                    var config = new org.springframework.web.cors
//                            .CorsConfiguration();
//                    config.setAllowedOrigins(List.of(
//                            "http://localhost:3000",
//                            "http://localhost:5173"));
//                    config.setAllowedMethods(List.of(
//                            "GET", "POST", "PUT",
//                            "DELETE", "OPTIONS", "PATCH"));
//                    config.setAllowedHeaders(List.of("*"));
//                    config.setAllowCredentials(true);
//                    return config;
//                }))
        http.cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // all member endpoints protected
                        .requestMatchers("/members/**")
                        .hasAuthority("ROLE_MEMBER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}