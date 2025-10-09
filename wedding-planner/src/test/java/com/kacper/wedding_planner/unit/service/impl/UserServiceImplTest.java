package com.kacper.wedding_planner.unit.service.impl;

import com.kacper.wedding_planner.exception.UserAlreadyExistsException;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.EmailService;
import com.kacper.wedding_planner.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private UserServiceImpl userService;

    private final String email = "test@example.com";
    private final String password = "password123";
    private final String encodedPassword = "encoded123";
    private final String firstName = "Kacper";

    @BeforeEach
    void setUp() {

    }

    @Test
    void testRegisterUserSuccessfulRegistration() {

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        userService.registerUser(email, password, firstName);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(email, savedUser.getEmail());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertEquals(firstName, savedUser.getFirstName());

        verify(emailService).sendWelcomeEmail(email, firstName);
    }

    @Test
    void testRegisterUserEmailAlreadyExistsThrowsException() {

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(email, password, firstName);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindByEmailUserFound() {

        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(email);

        assertEquals(email, result.getEmail());
    }

    @Test
    void testFindByEmailUserNotFoundThrowsException() {

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
            userService.findByEmail(email)
        );
    }
}