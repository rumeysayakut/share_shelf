
package com.shareshelf.service;

import com.shareshelf.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessExpirationMs;

    public JwtService(
            @Value("${JWT_SECRET}") String secret,
            @Value("${JWT_ACCESS_EXPIRATION_MS}") long accessExpirationMs) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpirationMs = accessExpirationMs;
    }

    public String generateAccessToken(User user) {

        Date now = new Date();
        Date expiration = new Date(
                now.getTime() + accessExpirationMs
        );

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {

        String subject = extractAllClaims(token).getSubject();

        return Long.parseLong(subject);
    }

    public boolean isTokenValid(String token, User user) {

        try {
            Claims claims = extractAllClaims(token);

            return claims.getSubject().equals(user.getId().toString())
                    && claims.getExpiration().after(new Date())
                    && user.getDeletedAt() == null;

        } catch (io.jsonwebtoken.JwtException
                 | IllegalArgumentException exception) {

            return false;
        }
    }
}
