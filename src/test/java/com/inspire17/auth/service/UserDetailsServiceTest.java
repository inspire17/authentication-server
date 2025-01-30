package com.inspire17.auth.service;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.exceptions.ServerException;
import com.inspire17.auth.model.UserRole;
import com.inspire17.auth.model.UserWrapper;
import com.inspire17.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito for JUnit 5
class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setUserRole(UserRole.USERS);
    }

    @Test
    void loadUserByUsername_UserExists_ShouldReturnUserWrapper() throws ServerException {
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserWrapper userWrapper = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userWrapper);
        assertEquals(testUser.getUsername(), userWrapper.getUsername());
        verify(userRepo, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_UserNotFound_ShouldReturnNull() throws ServerException {
        when(userRepo.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UserWrapper userWrapper = userDetailsService.loadUserByUsername("nonexistent");

        assertNull(userWrapper);
        verify(userRepo, times(1)).findByUsername("nonexistent");
    }

    @Test
    void saveUser_ShouldSaveUserSuccessfully() {
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        userDetailsService.saveUser(testUser);

        verify(userRepo, times(1)).save(testUser);
    }
}
