package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.ExpenseRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExpenseServiceImplTest {

    private ExpenseRepository expenseRepository;
    private UserRepository userRepository;
    private ExpenseServiceImpl expenseService;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        userRepository = mock(UserRepository.class);
        expenseService = new ExpenseServiceImpl(expenseRepository, userRepository);
    }

    @Test
    void shouldReturnExpensesForUser() {
        User user = new User();
        user.setEmail("test@example.com");
        Expense expense = new Expense();
        expense.setNazwa("Test");
        expense.setKwota(BigDecimal.valueOf(100));
        expense.setUser(user);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(expenseRepository.findByUser(user)).thenReturn(Collections.singletonList(expense));

        var result = expenseService.getExpensesForUser("test@example.com");

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getNazwa());
    }

}