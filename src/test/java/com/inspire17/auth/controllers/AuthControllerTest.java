package com.inspire17.auth.controllers;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.model.SignupRequest;
import com.inspire17.auth.model.UserRole;
import com.inspire17.auth.sec.JwtUtil;
import com.inspire17.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setUserRole(UserRole.USERS);

        signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setPassword("password");

    }

    @Test
    void refreshToken_ValidToken_ShouldReturnNewAccessToken() {
        String refreshToken = "valid_refresh_token";
        String newAccessToken = "new_access_token";

        when(jwtUtil.refreshToken(refreshToken)).thenReturn(newAccessToken);

        ResponseEntity<?> response = authController.refresh_token(refreshToken);

        assertEquals(OK, response.getStatusCode());
        assertEquals(newAccessToken, response.getBody());
    }

    @Test
    void refreshToken_InvalidToken_ShouldReturnUnauthorized() {
        String refreshToken = "invalid_refresh_token";

        when(jwtUtil.refreshToken(refreshToken)).thenReturn(null);

        ResponseEntity<?> response = authController.refresh_token(refreshToken);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid refresh token", response.getBody());
    }

    @Test
    void signup_ValidUser_ShouldReturnSuccessMessage() {
        doNothing().when(jwtService).signup(signupRequest);

        ResponseEntity<?> response = authController.signup(signupRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    void usernameCheck_ExistingUser_ShouldReturnExistsMessage() {
        when(jwtService.userNameExits("testuser")).thenReturn(true);

        ResponseEntity<?> response = authController.usernameCheck("testuser");

        assertEquals(OK, response.getStatusCode());
        assertEquals("Username exists", response.getBody());
    }

    @Test
    void usernameCheck_NonExistingUser_ShouldReturnAvailableMessage() {
        when(jwtService.userNameExits("newuser")).thenReturn(false);

        ResponseEntity<?> response = authController.usernameCheck("newuser");

        assertEquals(OK, response.getStatusCode());
        assertEquals("UserName available", response.getBody());
    }

    @Test
    void login_ValidCredentials_ShouldReturnToken() {
        User authenticatedUser = new User();
        authenticatedUser.setUsername("testuser");
        authenticatedUser.setUserRole(UserRole.USERS);
        String token = "generated_token";

        when(jwtService.authenticate(testUser)).thenReturn(authenticatedUser);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(token);

        ResponseEntity<?> response = authController.login(testUser);

        assertEquals(OK, response.getStatusCode());
        assertEquals(token, response.getBody());
    }

    @Test
    void login_InvalidCredentials_ShouldReturnUnauthorized() {
        when(jwtService.authenticate(testUser)).thenReturn(null);

        ResponseEntity<?> response = authController.login(testUser);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }
}
