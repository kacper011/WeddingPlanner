package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.PasswordChangeDTO;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showAccountPage(Model model) {
        model.addAttribute("passwordChangeDto", new PasswordChangeDTO());
        return "account";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("passwordChangeDto") @Valid PasswordChangeDTO passwordChangeDTO,
                                 BindingResult result,
                                 @AuthenticationPrincipal CustomUserDetails principal,
                                 Model model) {
        if (result.hasErrors()) {
            return "account";
        }

        User user = userService.findByEmail(principal.getUsername());

        try {
            userService.changePassword(
                    user,
                    passwordChangeDTO.getOldPassword(),
                    passwordChangeDTO.getNewPassword(),
                    passwordChangeDTO.getConfirmPassword()
            );

            model.addAttribute("success", "Hasło zostało zmienione!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "account";
    }
}
