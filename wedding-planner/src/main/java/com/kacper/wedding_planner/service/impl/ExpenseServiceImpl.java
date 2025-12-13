package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.exception.ExpenseNotFoundException;
import com.kacper.wedding_planner.exception.InvalidExpenseDataException;
import com.kacper.wedding_planner.exception.UnauthorizedExpenseAccessException;
import com.kacper.wedding_planner.exception.UserNotFoundException;
import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.ExpenseRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.ExpenseService;
import jakarta.transaction.Transactional;
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
    public List<Expense> getExpensesForUser(User user) {
        return expenseRepository.findByUser(user);
    }

    @Override
    public BigDecimal getTotalForUser(User user) {
        List<Expense> expenses = getExpensesForUser(user);

        boolean hasNullFields = expenses.stream()
                .anyMatch(e -> e.getAmount() == null || e.getName() == null);

        if (hasNullFields) {
            throw new InvalidExpenseDataException();
        }

        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void saveExpense(Expense expense) {
        if (expense.getAmount() == null ||
                expense.getName() == null ||
                expense.getName().trim().isEmpty()) {
            throw new InvalidExpenseDataException();
        }

        expenseRepository.save(expense);
    }

    @Override
    public void deleteExpenseByIdAndUser(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedExpenseAccessException(user.getEmail());
        }

        expenseRepository.delete(expense);
    }
}
