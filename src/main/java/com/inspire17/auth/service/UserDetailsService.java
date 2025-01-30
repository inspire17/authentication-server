package com.inspire17.auth.service;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.exceptions.ServerException;
import com.inspire17.auth.model.UserWrapper;
import com.inspire17.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserWrapper loadUserByUsername(String username) throws ServerException {
        logger.info("Loading user by username: {}", username);

        Optional<User> user = userRepo.findByUsername(username);

        if (user.isPresent()) {
            logger.debug("User found: {}", username);
            return new UserWrapper(user.get());
        } else {
            logger.warn("User not found: {}", username);
            return null;
        }
    }

    public void saveUser(User user) {
        logger.info("Saving user: {}", user.getUsername());

        try {
            userRepo.save(user);
            logger.debug("User saved successfully: {}", user.getUsername());
        } catch (DataIntegrityViolationException e) {
            logger.error("Validation failed: {}", user.getUsername(), e);
            throw new ServerException("Validation failed", 400);
        }
    }
}
