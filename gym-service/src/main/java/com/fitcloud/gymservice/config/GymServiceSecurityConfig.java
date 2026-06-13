package com.fitcloud.gymservice.config;


import com.fitcloud.gymservice.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class GymServiceSecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.cors(cors -> cors.configurationSource(request -> {
//                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
//                    corsConfiguration.setAllowedOrigins(java.util.List.of("http://localhost:3000", "http://localhost:5173")); // Your UI URL
//                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
//                    corsConfiguration.setAllowCredentials(true); // Required for Cookies/Sessions
//                    return corsConfiguration;
//                }))
        http.cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // ── public endpoints ──────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/gyms", "/gyms/{id}",
                                "/gyms/{id}/equipment",
                                "/gyms/{id}/plans",
                                "/gyms/{id}/reviews")
                        .permitAll()

                        // ── gym owner endpoints ───────────────
                        .requestMatchers(HttpMethod.POST, "/gyms/**")
                        .hasAuthority("ROLE_GYM_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/gyms/**")
                        .hasAuthority("ROLE_GYM_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/gyms/**")
                        .hasAuthority("ROLE_GYM_OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/gyms/**")
                        .hasAuthority("ROLE_GYM_OWNER")

                        // ── member endpoints ──────────────────
                        .requestMatchers("/memberships/**")
                        .hasAuthority("ROLE_MEMBER")

                        // ── Admin endpoints ──────────────────
                        .requestMatchers(HttpMethod.POST, "/admin/gyms/**")
                        .hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }


}

//JwtAuthFilter exists but Spring Security
//does not know about it yet
//
//You need to tell Spring Security:
//        "run JwtAuthFilter BEFORE you check
//username and password"
//
//Without this line:
//JwtAuthFilter is never called
//Token is never read
//Every request → 401 ❌
//
//With this line:
//JwtAuthFilter runs first
//Sets authentication in context
//Spring Security then checks roles ✅
