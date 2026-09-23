package com.ridelink.account_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

        private final SecretKey secretKey;
        private final long expirationTime;

        public JwtService(
                        @Value("${jwt.secret}") String secret,
                        @Value("${jwt.expiration}") long expirationTime) {

                this.secretKey = Keys.hmacShaKeyFor(
                                secret.getBytes(StandardCharsets.UTF_8));

                this.expirationTime = expirationTime;
        }

        public String generateToken(String userId, String email, String role) {

                Date now = new Date();
                Date expirationDate = new Date(
                                now.getTime() + expirationTime);

                return Jwts.builder()
                                .subject(userId)
                                .claim("email", email)
                                .claim("role", role)
                                .issuedAt(now)
                                .expiration(expirationDate)
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
}