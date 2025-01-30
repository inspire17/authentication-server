package com.inspire17.auth.service;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.exceptions.ServerException;
import com.inspire17.auth.model.AccountStatus;
import com.inspire17.auth.model.SignupRequest;
import com.inspire17.auth.model.UserRole;
import com.inspire17.auth.model.UserWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public void signup(SignupRequest signupRequest) throws ServerException {
        logger.info("Starting user signup process for username: {}", signupRequest.getUsername());

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmailId(signupRequest.getEmailId());
        user.setMobileNumber(signupRequest.getMobileNumber());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUserRole(signupRequest.getUserRole() != null ? signupRequest.getUserRole() : UserRole.USERS);
        user.setAccountStatus(AccountStatus.PENDING);
        user.setIsEmailVerified(false);

        try {
            userDetailsService.saveUser(user);
            logger.info("User registered successfully: {}", user.getUsername());
        } catch (Exception e) {
            logger.error("Error during user signup for username: {}", user.getUsername(), e);
            throw new ServerException("Failed to register user", 400);
        }
    }

    public boolean userNameExits(String name) throws ServerException {
        logger.info("Checking if username exists: {}", name);

        UserWrapper userWrapper = userDetailsService.loadUserByUsername(name);
        boolean exists = userWrapper != null;

        if (exists) {
            logger.debug("Username exists: {}", name);
        } else {
            logger.debug("Username is available: {}", name);
        }

        return exists;
    }

    public User authenticate(User input) {
        logger.info("Authenticating user: {}", input.getUsername());

        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getUsername(),
                            input.getPassword()
                    )
            );
        } catch (Exception e) {
            logger.warn("Authentication failed for user: {}", input.getUsername());
            return null;
        }

        if (authenticate.isAuthenticated()) {
            logger.info("User authenticated successfully: {}", input.getUsername());
            return input;
        }

        logger.warn("Authentication unsuccessful for user: {}", input.getUsername());
        return null;
    }
}
