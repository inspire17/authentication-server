package com.inspire17.auth.repository;

import com.inspire17.auth.entity.User;
import com.inspire17.auth.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // Loads only JPA components for testing
@ActiveProfiles("test")  // Uses application-test.properties
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)  // Ensures H2 is used
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private HandlerExceptionResolver handlerExceptionResolver;


    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmailId("myemail@test.com");
        user.setPassword("securepassword");
        user.setIsEmailVerified(false);
        user.setUserRole(UserRole.USERS);
        userRepository.save(user);
    }

    @Test
    void shouldFindUserByUsername() {
        Optional<User> user = userRepository.findByUsername("testuser");
        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldReturnEmptyForNonExistentUser() {
        Optional<User> user = userRepository.findByUsername("random");
        assertThat(user).isNotPresent();
    }

    @Test
    void shouldFindUserByEmail() {
        Optional<User> user = userRepository.findByEmailId("myemail@test.com");
        assertThat(user).isPresent();
        assertThat(user.get().getEmailId()).isEqualTo("myemail@test.com");
    }

    @Test
    void shouldReturnEmptyForNonExistentEmail() {
        Optional<User> user = userRepository.findByEmailId("random");
        assertThat(user).isNotPresent();
    }
}