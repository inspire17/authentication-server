package com.inspire17.auth.controllers;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.model.AccountStatus;
import com.inspire17.auth.model.SignupRequest;
import com.inspire17.auth.model.UserRole;
import com.inspire17.auth.model.UserWrapper;
import com.inspire17.auth.sec.JwtUtil;
import com.inspire17.auth.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for authentication and user management endpoints.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshToken The refresh token used to generate a new access token.
     * @return A new access token if the refresh token is valid, otherwise returns an error response.
     */
    @PostMapping("/refresh_token")
    public ResponseEntity<?> refresh_token(@RequestBody String refreshToken) {
        logger.info("Received request to refresh token");
        String newAccessToken = jwtUtil.refreshToken(refreshToken);

        if (newAccessToken == null) {
            logger.warn("Invalid refresh token received");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        logger.info("Successfully refreshed token");
        return ResponseEntity.ok(newAccessToken);
    }

    /**
     * Handles user signup and registration.
     *
     * @param signupRequest The user details for registration.
     * @return A success message upon successful registration.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        logger.info("Received signup request for username: {}", signupRequest.getUsername());

        jwtService.signup(signupRequest);
        logger.info("User registered successfully: {}", signupRequest.getUsername());
        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Checks whether a given username is available.
     *
     * @param q The text to be checked for available username.
     * @return A response indicating whether the username exists or is available.
     */
    @GetMapping("/username_check")
    public ResponseEntity<?> usernameCheck(@RequestParam(required = true) String q) {

        if (jwtService.userNameExits(q)) {
            logger.debug("Username exists: {}", q);
            return ResponseEntity.ok("Username exists");
        } else {
            logger.debug("Username available: {}", q);
            return ResponseEntity.ok("UserName available");
        }
    }

    /**
     * Handles user login and returns a JWT token upon successful authentication.
     *
     * @param user The user credentials.
     * @return A JWT token if authentication is successful, otherwise an unauthorized response.
     */
    @PostMapping("/user_login")
    public ResponseEntity<?> login(@RequestBody User user) {
        logger.info("Login attempt for username: {}", user.getUsername());

        user.setUserRole(UserRole.USERS);

        User existingUser = jwtService.authenticate(user);

        if (existingUser != null) {
            UserDetails userDetails = new UserWrapper(existingUser);
            String token = jwtUtil.generateToken(userDetails);
            logger.info("User {} logged in successfully, token generated", user.getUsername());
            return ResponseEntity.ok(token);
        }

        logger.warn("Invalid login attempt for username: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
