package com.bcupen.pocket_coach_service.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleApiException_shouldReturnBadRequestResponse() {
        ApiException ex = new ApiException(HttpStatus.BAD_REQUEST, "Custom error");
        ResponseEntity<ApiResponse<Void>> response = handler.handleApiException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("Custom error", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void handleGeneric_shouldReturnInternalServerError() {
        Exception ex = new RuntimeException("Oops");
        ResponseEntity<ApiResponse<Void>> response = handler.handleUnhandled(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("Something went wrong", response.getBody().getMessage());
    }
}

