package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.service.ExpenseService;
import com.kacper.wedding_planner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;

    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @GetMapping
    public String showExpenses(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        List<Expense> expenses = expenseService.getExpensesForUser(principal.getUsername());
        BigDecimal total = expenseService.getTotalForUser(principal.getUsername());

        model.addAttribute("expenses", expenses);
        model.addAttribute("total", total);
        model.addAttribute("expense", new Expense());

        return "expenses";
    }

    @PostMapping
    public String saveExpense(@ModelAttribute @Valid Expense expense, BindingResult result, @AuthenticationPrincipal CustomUserDetails principal, Model model) {

        if (result.hasErrors()) {
            List<Expense> expenses = expenseService.getExpensesForUser(principal.getUsername());
            BigDecimal total = expenseService.getTotalForUser(principal.getUsername());

            model.addAttribute("expenses", expenses);
            model.addAttribute("total", total);

            return "expenses";
        }

        User user = userService.findByEmail(principal.getUsername());
        expenseService.saveExpense(expense, user);

        return "redirect:/expenses";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        expenseService.deleteExpenseByIdAndUser(id, principal.getUsername());
        return "redirect:/expenses";
    }
}
