package com.fitcloud.gymservice.config;


import com.fitcloud.gymservice.service.CustomUserDetailsService;
import com.fitcloud.gymservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // load UserDetails shell (no DB)
            UserDetails userDetails =
                    customUserDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, username)) {

                // ← extract role from token NOT from userDetails
                String role = jwtService.extractRole(token);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(new SimpleGrantedAuthority(role)) // ← FIXED
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
//Every request to a protected endpoint
//must carry a JWT token
//
//Without JwtAuthFilter:
//Request → Spring Security → "who are you?" → 401
//
//With JwtAuthFilter:
//Request → JwtAuthFilter reads token
//        → extracts username
//        → loads user from DB
//        → validates token
//        → tells Spring Security "this is John, ROLE_ADMIN"
//        → Spring Security allows request ✅
//
//It runs on EVERY request automatically
//before Spring Security checks permissions
