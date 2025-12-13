package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseService {

    List<Expense> getExpensesForUser(User user);

    BigDecimal getTotalForUser(User user);

    void saveExpense(Expense expense);

    void deleteExpenseByIdAndUser(Long id, User user);
}
