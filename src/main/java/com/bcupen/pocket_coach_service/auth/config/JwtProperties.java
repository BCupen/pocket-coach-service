package com.bcupen.pocket_coach_service.auth.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Validated
public class JwtProperties {

    @NotBlank(message = "JWT secret must not be blank")
    private String secret;

    @Min(value = 1000, message = "JWT expiration must be at least 1000 milliseconds")
    private long expirationMs;

    @Min(value = 1000, message = "JWT refresh token expiration must be at least 1000 milliseconds")
    private long refreshExpirationMs;
}
