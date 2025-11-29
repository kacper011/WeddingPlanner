package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.NameChangeDTO;
import com.kacper.wedding_planner.dto.PasswordChangeDTO;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping
    public String showAccountPage(Model model) {
        model.addAttribute("passwordChangeDto", new PasswordChangeDTO());
        return "account";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordChangeDto", new PasswordChangeDTO());
        return "account_change_password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("passwordChangeDto") @Valid PasswordChangeDTO passwordChangeDTO,
                                 BindingResult result,
                                 @AuthenticationPrincipal CustomUserDetails principal,
                                 Model model) {
        if (result.hasErrors()) {
            return "account_change_password";
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
        } catch (Exception e) {
            log.error("Błąd zmiany hasła dla użytkownika {}", user.getEmail(), e);
            model.addAttribute("error", "Wystąpił błąd podczas zmiany hasła. Spróbuj ponownie.");
        }

        return "account_change_password";
    }

    @GetMapping("/change-name")
    public String showChangeNameForm(Model model) {
        model.addAttribute("nameChangeDto", new NameChangeDTO());
        return "account_change_name";
    }

    @PostMapping("/change-name")
    public String changeName(@ModelAttribute("nameChangeDto") @Valid NameChangeDTO nameChangeDTO,
                             BindingResult result,
                             @AuthenticationPrincipal CustomUserDetails principal,
                             Model model) {
        if (result.hasErrors()) {
            return "account_change_name";
        }

        User user = userService.findByEmail(principal.getUsername());

        try {
            user.setFirstName(nameChangeDTO.getFirstName());
            userService.save(user);
            model.addAttribute("success", "Imię zostało zmienione!");
        } catch (Exception e) {
            log.error("Błąd zmiany imienia dla użytkownika {}", user.getEmail(), e);
            model.addAttribute("error", "Wystąpił błąd podczas zmiany imienia. Spróbuj ponownie.");
        }

        return "account_change_name";
    }

    @GetMapping("/delete")
    public String showDeleteAccountPage() {
        return "account_confirm_delete";
    }

    @PostMapping("/delete")
    public String deleteAccount(@AuthenticationPrincipal CustomUserDetails principal,
                                @RequestParam String password,
                                @RequestParam String confirmation,
                                Model model) {
        User user = userService.findByEmail(principal.getUsername());

        if (!"USUWAM".equals(confirmation)) {
            model.addAttribute("error", "Musisz wpisać dokładnie: USUWAM");
            return "account_confirm_delete";
        }

        if (!userService.checkPassword(user, password)) {
            model.addAttribute("error", "Nieprawidłowe hasło.");
            return "account_confirm_delete";
        }

        try {
            userService.deleteUser(user);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("Błąd usuwania konta użytkownika {}", user.getEmail(), e);
            model.addAttribute("error", "Wystąpił błąd podczas usuwania konta. Spróbuj ponownie.");
            return "account_confirm_delete";
        }

        return "redirect:/login?deleted";
    }
}
