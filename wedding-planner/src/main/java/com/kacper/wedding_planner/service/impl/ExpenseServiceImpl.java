package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.ExpenseRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.ExpenseService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Expense> getExpensesForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Nie ma takiego użytkownika."));

        return expenseRepository.findByUser(user);
    }

    @Override
    public BigDecimal getTotalForUser(String userEmail) {
        List<Expense> expenses = getExpensesForUser(userEmail);

        return expenses.stream()
                .map(Expense::getKwota)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void saveExpense(Expense expense, User user) {
        expense.setUser(user);
        expenseRepository.save(expense);
    }
}
