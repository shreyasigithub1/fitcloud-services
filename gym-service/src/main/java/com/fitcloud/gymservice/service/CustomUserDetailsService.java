package com.fitcloud.gymservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // ✅ NO repository
    // ✅ NO imports from other services
    // ✅ NO DB lookup

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Just create a shell UserDetails with the username
        // Role is set separately in JwtAuthFilter
        // from the JWT token claims
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("")
                .authorities(Collections.emptyList())
                .build();
    }
}