package com.bcupen.pocket_coach_service.auth;

import com.bcupen.pocket_coach_service.auth.config.SecurityConfig;
import com.bcupen.pocket_coach_service.auth.controllers.AuthController;
import com.bcupen.pocket_coach_service.auth.dtos.CreateUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.CreateUserResponse;

import com.bcupen.pocket_coach_service.auth.dtos.LoginUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.LoginUserResponse;
import com.bcupen.pocket_coach_service.auth.services.UserService;
import com.bcupen.pocket_coach_service.common.ApiException;
import com.bcupen.pocket_coach_service.common.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class}) // import test config for manual mocks
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService; // our manual mock

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void debugServiceInjection() {
        System.out.println("userService class: " + userService.getClass().getName());
    }


    @Test
    void createUser_shouldReturn201WithSuccess() throws Exception {
        var request = new CreateUserRequest("john", "john@email.com", "P@ssword1");
        var response = new CreateUserResponse("john@email.com", "john");

        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/auth/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User created"))
                .andExpect(jsonPath("$.data.username").value("john"));
    }

    @Test
    void createUser_shouldReturn500WhenServiceThrows() throws Exception {
        var request = new CreateUserRequest("john", "john@email.com", "V@lid123");

        when(userService.createUser(any())).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/auth/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }

    @Test
    void loginUser_shouldReturn200WithSuccess() throws Exception {
        var request = new LoginUserRequest("testemail@email.com", "P@ssword1");
        var response = new LoginUserResponse("testemail@email.com", "testaccesstoken", "testrefreshtoken", 3600);

        when(userService.loginUser(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.email").value("testemail@email.com"));
    }

    @Test
    void loginUser_shouldReturn401WhenInvalidCredentials() throws Exception {
        var request = new LoginUserRequest("invalid@email.com", "wrongpassword");

        when(userService.loginUser(any())).thenThrow(new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void loginUser_shouldReturn500WhenServiceThrows() throws Exception {
        var request = new LoginUserRequest("something@email.com", "P@ssword1");

        when(userService.loginUser(any())).thenThrow(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}
