package com.kacper.wedding_planner.config;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MyUserDetailsServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final MyUserDetailsService service = new MyUserDetailsService(userRepository);

    @Test
    void loadUserByUsernameReturnsCustomUserDetailsWhenUserExists() {

        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        var result = service.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        verify(userRepository).findByEmail("test@example.com");
    }
}