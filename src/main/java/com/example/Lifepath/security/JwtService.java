package com.example.Lifepath.security;

import com.example.Lifepath.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expiration;

    public JwtService(@Value("${app.jwt.secret:}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expiration) {
        if (secret == null || secret.length() < 32)
            throw new IllegalStateException("JWT_SECRET must be configured with at least 32 characters");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String create(User u) {
        long now = System.currentTimeMillis();
        return Jwts.builder().subject(u.getId().toString()).claim("email", u.getEmail())
                .claim("role", u.getRole().name()).issuedAt(new Date(now)).expiration(new Date(now + expiration))
                .signWith(key).compact();
    }

    public Long userId(String token) {
        return Long.valueOf(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());
    }
}
