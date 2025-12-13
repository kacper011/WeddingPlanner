package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.ExpenseRequest;
import com.kacper.wedding_planner.mapper.ExpenseMapper;
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
    public String showExpenses(Model model,
                               @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());

        List<Expense> expenses = expenseService.getExpensesForUser(user);
        BigDecimal total = expenseService.getTotalForUser(user);

        model.addAttribute("expenses", expenses);
        model.addAttribute("total", total);
        model.addAttribute("expense", new ExpenseRequest());

        return "expenses";
    }

    @PostMapping
    public String saveExpense(@ModelAttribute @Valid ExpenseRequest request,
                              BindingResult result,
                              @AuthenticationPrincipal CustomUserDetails principal,
                              Model model) {

        User user = userService.findByEmail(principal.getUsername());

        if (result.hasErrors()) {
            model.addAttribute("expenses", expenseService.getExpensesForUser(user));
            model.addAttribute("total", expenseService.getTotalForUser(user));
            return "expenses";
        }

        Expense expense = ExpenseMapper.toEntity(request, user);
        expenseService.saveExpense(expense);

        return "redirect:/expenses";
    }

    @PostMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());
        expenseService.deleteExpenseByIdAndUser(id, user);

        return "redirect:/expenses";
    }
}
