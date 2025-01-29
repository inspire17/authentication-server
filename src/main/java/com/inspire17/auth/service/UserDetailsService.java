package com.inspire17.auth.service;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.exceptions.ServerRequestFailed;
import com.inspire17.auth.model.UserWrapper;
import com.inspire17.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepo;


    @Override
    public UserWrapper loadUserByUsername(String username) throws ServerRequestFailed {
        Optional<User> user = userRepo.findByUsername(username);
        return user.map(UserWrapper::new).orElse(null);
    }

    public void saveUser(User user) {
        userRepo.save(user);
    }
}