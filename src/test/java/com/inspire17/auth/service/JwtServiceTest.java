package com.inspire17.auth.service;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.exceptions.ServerException;
import com.inspire17.auth.model.SignupRequest;
import com.inspire17.auth.model.UserRole;
import com.inspire17.auth.model.UserWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private JwtService jwtService;

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
        signupRequest.setUserRole(UserRole.USERS);
    }

    @Test
    void signup_ValidUser_ShouldSaveUser() throws ServerException {
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encoded_password");
        doNothing().when(userDetailsService).saveUser(any(User.class));

        assertDoesNotThrow(() -> jwtService.signup(signupRequest));

        verify(passwordEncoder, times(1)).encode(testUser.getPassword());
        verify(userDetailsService, times(1)).saveUser(any(User.class));
    }

    @Test
    void signup_Failure_ShouldThrowException() {
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encoded_password");
        doThrow(new RuntimeException("Database error")).when(userDetailsService).saveUser(any(User.class));

        ServerException exception = assertThrows(ServerException.class, () -> jwtService.signup(signupRequest));
        assertEquals("Failed to register user", exception.getMessage());

        verify(passwordEncoder, times(1)).encode(testUser.getPassword());
        verify(userDetailsService, times(1)).saveUser(any(User.class));
    }

    @Test
    void userNameExits_ExistingUser_ShouldReturnTrue() throws ServerException {
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(new UserWrapper(testUser));

        assertTrue(jwtService.userNameExits("testuser"));

        verify(userDetailsService, times(1)).loadUserByUsername("testuser");
    }

    @Test
    void userNameExits_NonExistingUser_ShouldReturnFalse() throws ServerException {
        when(userDetailsService.loadUserByUsername("newuser")).thenReturn(null);

        assertFalse(jwtService.userNameExits("newuser"));

        verify(userDetailsService, times(1)).loadUserByUsername("newuser");
    }

    @Test
    void authenticate_ValidCredentials_ShouldReturnUser() {
        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);

        User authenticatedUser = jwtService.authenticate(testUser);

        assertNotNull(authenticatedUser);
        assertEquals(testUser.getUsername(), authenticatedUser.getUsername());
    }

    @Test
    void authenticate_InvalidCredentials_ShouldReturnNull() {
        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
        when(mockAuthentication.isAuthenticated()).thenReturn(false);

        User authenticatedUser = jwtService.authenticate(testUser);

        assertNull(authenticatedUser);
    }
}
