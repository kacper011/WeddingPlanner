package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.exception.UserNotFoundException;
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
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        return expenseRepository.findByUser(user);
    }

    @Override
    public BigDecimal getTotalForUser(String userEmail) {
        List<Expense> expenses = getExpensesForUser(userEmail);

        boolean hasNullFields = expenses.stream()
                .anyMatch(expense -> expense.getKwota() == null || expense.getNazwa() == null);

        if (hasNullFields) {
            throw new IllegalArgumentException("Należy uzupełnić wszystkie pola wydatków (kwota i nazwa)");
        }

        return expenses.stream()
                .map(Expense::getKwota)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void saveExpense(Expense expense, User user) {
        expense.setUser(user);
        expenseRepository.save(expense);
    }

    @Override
    public void deleteExpenseByIdAndUser(Long id, String username) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wydatek nieznaleziony."));

        if (!expense.getUser().getEmail().equals(username)) {
            throw new RuntimeException("Nie jesteś autoryzowany do usunięcia tego wydatku.");
        }

        expenseRepository.delete(expense);
    }
}
