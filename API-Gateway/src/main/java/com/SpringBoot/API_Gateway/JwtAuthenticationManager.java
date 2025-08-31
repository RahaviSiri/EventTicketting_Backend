package com.SpringBoot.API_Gateway;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final String SECRET = "rG7s5f8h2j9k1l3p6q4t7w8x0y2z5v7b"; // must match UserService

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();

            // ✅ Check expiration
            if (claims.getExpiration().before(new Date())) {
                return Mono.empty(); // expired token
            }

            String email = claims.getSubject();

            return Mono.just(
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList())
            );
        } catch (JwtException e) {
            return Mono.empty(); // invalid token
        }
    }
}
