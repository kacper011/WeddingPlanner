package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseService {

    public List<Expense> getExpensesForUser(String userEmail);

    public BigDecimal getTotalForUser(String userEmail);
    void saveExpense(Expense expense, User user);
}
