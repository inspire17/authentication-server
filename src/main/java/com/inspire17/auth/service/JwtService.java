package com.inspire17.auth.service;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.exceptions.ServerRequestFailed;
import com.inspire17.auth.model.UserWrapper;
import com.inspire17.auth.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class JwtService {
    @Autowired
    private UserDetailsService fgUserDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;


    public void signup(User input) throws ServerRequestFailed {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setFgUserRole(UserRole.USERS);
        try {
            fgUserDetailsService.saveUser(user);
        } catch (Exception e) {
            throw new ServerRequestFailed("Failed to register user", 400);
        }
    }

    public boolean userNameExits(String name) throws ServerRequestFailed {
        UserWrapper fgUser = fgUserDetailsService.loadUserByUsername(name);
        return fgUser != null;
    }

    public User authenticate(User input) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );

        if (authenticate.isAuthenticated()) {
            return input;
        }

        return null;
    }
}