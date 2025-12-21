package com.kacper.wedding_planner.unit.service.impl;

import com.kacper.wedding_planner.exception.UserNotFoundException;
import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.ExpenseRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExpenseServiceImplTest {

    private ExpenseRepository expenseRepository;
    private ExpenseServiceImpl expenseService;
    private User user;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        expenseService = new ExpenseServiceImpl(expenseRepository, null);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void shouldReturnExpensesForUser() {

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setName("Test");
        expense.setAmount(BigDecimal.valueOf(100));

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of(expense));

        var result = expenseService.getExpensesForUser(user);

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getName());
    }

    @Test
    void shouldCalculateTotalExpensesCorrectly() {

        Expense e1 = new Expense();
        e1.setAmount(BigDecimal.valueOf(100));
        e1.setName("Test1");

        Expense e2 = new Expense();
        e2.setAmount(BigDecimal.valueOf(200));
        e2.setName("Test2");

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of(e1, e2));

        BigDecimal total = expenseService.getTotalForUser(user);

        assertEquals(BigDecimal.valueOf(300), total);
    }

    @Test
    void shouldThrowIfExpenseHasNullFields() {

        Expense expense = new Expense();
        expense.setAmount(null);
        expense.setName("Test1");

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of(expense));

        assertThrows(IllegalArgumentException.class,
                () -> expenseService.getTotalForUser(user));
    }

    @Test
    void shouldSaveExpenseWithUser() {

        Expense expense = new Expense();
        expense.setName("Test");
        expense.setAmount(BigDecimal.valueOf(50));

        expenseService.saveExpense(expense);

        verify(expenseRepository).save(expense);
    }

    @Test
    void shouldDeleteExpenseIfAuthorized() {

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUser(user);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        expenseService.deleteExpenseByIdAndUser(1L, user);

        verify(expenseRepository).delete(expense);
    }

    @Test
    void shouldThrowIfDeletingUnauthorizedExpense() {
        User otherUser = new User();
        otherUser.setId(2L);

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUser(otherUser);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThrows(RuntimeException.class,
                () -> expenseService.deleteExpenseByIdAndUser(1L, user));
    }

    @Test
    void shouldThrowIfExpenseToDeleteNotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> expenseService.deleteExpenseByIdAndUser(1L, user));
    }
}