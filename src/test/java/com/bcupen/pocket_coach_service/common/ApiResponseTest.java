package com.bcupen.pocket_coach_service.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void success_shouldReturnCorrectResponse() {
        String data = "User created";
        ApiResponse<String> response = ApiResponse.success("Success", data);

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void error_shouldReturnCorrectErrorResponse() {
        ApiResponse<Void> response = ApiResponse.error("Something went wrong");

        assertFalse(response.isSuccess());
        assertEquals("Something went wrong", response.getMessage());
        assertNull(response.getData());
    }
}

