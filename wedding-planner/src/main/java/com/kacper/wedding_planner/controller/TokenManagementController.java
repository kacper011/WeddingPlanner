package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.model.UploadToken;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.UploadTokenRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tokens")
public class TokenManagementController {

    private final UploadTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public TokenManagementController(UploadTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        var tokens = tokenRepository.findAll().stream()
                .filter(t -> t.getOwner().getId().equals(user.getId()))
                .toList();

        model.addAttribute("tokens", tokens);

        return "tokens/list";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserDetails principal, @RequestParam(defaultValue = "48") int hours) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();

        UploadToken t = UploadToken.createForUser(user, hours);
        tokenRepository.save(t);

        return "redirect:/tokens";
    }

    // Unieważnianie tokena
    @PostMapping("/revoke/{id}")
    public String revoke(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        UploadToken t = tokenRepository.findById(id).orElseThrow();

        if (!t.getOwner().getId().equals(user.getId()))
            throw new SecurityException("Access denied");

        t.setActive(false);
        tokenRepository.save(t);

        return "redirect:/tokens";
    }
}
