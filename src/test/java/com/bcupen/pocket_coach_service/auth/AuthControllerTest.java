package com.bcupen.pocket_coach_service.auth;

import com.bcupen.pocket_coach_service.auth.controllers.AuthController;
import com.bcupen.pocket_coach_service.auth.dtos.CreateUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.CreateUserResponse;
import com.bcupen.pocket_coach_service.auth.services.UserService;
import com.bcupen.pocket_coach_service.common.ApiResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void createUser_shouldReturnSuccessResponse() throws Exception {
        try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
            // Arrange
            CreateUserRequest request = new CreateUserRequest("john", "john@email.com", "pass");
            CreateUserResponse expectedResponse = new CreateUserResponse("john@email.com", "john");

            when(userService.createUser(any())).thenReturn(expectedResponse);

            // Act
            ResponseEntity<ApiResponse<CreateUserResponse>> response = authController.createUser(request);

            // Assert
            assertEquals(201, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isSuccess());
            assertEquals("User created", response.getBody().getMessage());
            assertEquals("john", response.getBody().getData().getUsername());
        }
    }

//    @Test
//    void createUser_shouldReturnErrorResponse() throws Exception {
//        try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
//            // Arrange
//            CreateUserRequest request = new CreateUserRequest("john", "john@email.com", "pass");
//
//            when(userService.createUser(any())).thenThrow(new RuntimeException("DB error"));
//
//            // Act
//            ResponseEntity<ApiResponse<CreateUserResponse>> response = authController.createUser(request);
//
//            // Assert
//            assertEquals(500, response.getStatusCode().value());
//            assertNotNull(response.getBody());
//            assertFalse(response.getBody().isSuccess());
//            assertEquals("Failed to create user", response.getBody().getMessage());
//            assertNull(response.getBody().getData());
//        }
//    }
}
