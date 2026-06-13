package identity_service.Identity.Service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;  // ← Spring's @Value
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // Must be Base64 encoded, OR use Keys.secretKeyFor() to generate one
    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // ← use JJWT's own decoder
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username,Integer userId,
                                String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)   // ← gym service reads this
                .claim("role", role)       // ← gym service reads this
                .setIssuedAt(new Date())
                .setExpiration(new Date(
                        System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
//    Currently JwtService can only CREATE tokens
//    But cannot READ or VALIDATE them
//
//    When admin hits /admin/approve/1:
//    Request comes with token in header
//    Spring Security needs to:

//    ├── Extract username from token    ← extractUsername() needed
//    ├── Check token not expired        ← isTokenExpired() needed
//    └── Validate token is genuine      ← validateToken() needed
//
//    Without these methods
//    JwtAuthFilter cannot work
//    Every request returns 401

    // ── ADD THESE ───────────────────────────────────

    // Reads the username stored inside the token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();       // ← username stored here during generateToken
    }

    // Checks if token expiry date is before now
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    // Final validation — username matches AND not expired
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username)
                && !isTokenExpired(token);
    }
}
