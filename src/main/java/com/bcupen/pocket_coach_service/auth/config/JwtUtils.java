package com.bcupen.pocket_coach_service.auth.config;

import com.bcupen.pocket_coach_service.common.ApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtUtils {
    private final JwtProperties jwtProperties;

    private Key secretKey;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        try {
            this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
            log.info("JWT secret key initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize JWT secret key: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initialize JWT secret key");
        }
    }

    public String generateToken(String email, Map<String, Object> claims) {
        Date date = new Date();
        Date expirationDate = new Date(date.getTime() + jwtProperties.getExpirationMs());

        try{
            return Jwts.builder()
                    .setClaims(claims == null ? Map.of() : claims)
                    .setSubject(email)
                    .setIssuedAt(date)
                    .setExpiration(expirationDate)
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating JWT token: " + e.getMessage());
        }

    }

    public String getUserEmailFromToken(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (Exception e) {
            log.error("Error extracting email from JWT token: {}", e.getMessage());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + e.getMessage());
        }
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Malformed JWT token: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.warn("Invalid JWT signature: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT token compact of handler are invalid: {}", ex.getMessage());
        }
        return false;
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + e.getMessage());
        }
    }

}
