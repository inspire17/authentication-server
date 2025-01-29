package com.inspire17.auth.controllers;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.model.UserWrapper;
import com.inspire17.auth.model.UserRole;
import com.inspire17.auth.sec.JwtUtil;
import com.inspire17.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refresh_token(@RequestBody String refreshToken) {
        String newAccessToken = jwtUtil.refreshToken(refreshToken);
        if (newAccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        jwtService.signup(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/username_check")
    public ResponseEntity<?> usernameCheck(@RequestParam(required = true) String q) {
        if (jwtService.userNameExits(q)) {
            return ResponseEntity.ok("Username exists");
        } else {
            return ResponseEntity.ok("UserName available");
        }
    }


    @PostMapping("/user_login")
    public ResponseEntity<?> login(@RequestBody User user) {
        user.setFgUserRole(UserRole.USERS);
        User existingUser = jwtService.authenticate(user);
        if (existingUser != null) {
            UserDetails userDetails = new UserWrapper(existingUser);
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}

