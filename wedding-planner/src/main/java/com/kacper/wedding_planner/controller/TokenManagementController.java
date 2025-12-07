package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.model.UploadToken;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.UploadTokenRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

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
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var tokens = tokenRepository.findByOwner(user);

        model.addAttribute("tokens", tokens);

        return "tokens_list";
    }

    @GetMapping("/{id}/show")
    public String showToken(@AuthenticationPrincipal UserDetails principal,
                            @PathVariable Long id,
                            Model model) {

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        UploadToken t = tokenRepository.findById(id).orElseThrow();

        if (!t.getOwner().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");

        if (!t.isActive()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Token revoked");
        }

        if (t.getExpiresAt() != null && t.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Token expired");
        }

        String publicLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/public/upload/")
                .path(t.getToken())
                .toUriString();

        model.addAttribute("token", t);
        model.addAttribute("publicLink", publicLink);

        return "tokens_show";
    }

    @PostMapping("/create")
    public String create(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "48") int hours) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        UploadToken t = UploadToken.createForUser(user, hours);
        tokenRepository.save(t);

        return "redirect:/tokens/" + t.getId() + "/show";
    }

    @PostMapping("/revoke/{id}")
    public String revoke(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        UploadToken t = tokenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!t.getOwner().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");

        t.setActive(false);
        tokenRepository.save(t);

        return "redirect:/tokens";
    }
}
