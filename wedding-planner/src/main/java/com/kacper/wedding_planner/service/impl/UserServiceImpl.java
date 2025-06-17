package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.EmailService;
import com.kacper.wedding_planner.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public void registerUser(String email, String password, String firstName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email już istnieje.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);

        userRepository.save(user);

        emailService.sendWelcomeEmail(email, firstName);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
