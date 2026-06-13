package identity_service.Identity.Service.config;

import identity_service.Identity.Service.service.CustomUserDetailsService;
import identity_service.Identity.Service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

        // Step 1 — read Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2 — no token present → skip filter → Spring Security handles it
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3 — extract token by removing "Bearer " prefix
        String token = authHeader.substring(7);

        // Step 4 — extract username from inside the token
        String username = jwtService.extractUsername(token);

        // Step 5 — only proceed if username found and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6 — load full user details from DB
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Step 7 — validate token against user
            if (jwtService.validateToken(token, username)) {

                // Step 8 — create auth object with roles
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()  // ← ROLE_ADMIN etc
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Step 9 — tell Spring Security this user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 10 — continue to next filter
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