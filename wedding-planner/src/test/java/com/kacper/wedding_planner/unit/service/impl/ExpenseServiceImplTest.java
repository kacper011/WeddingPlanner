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
import java.util.Optional;

import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;
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
        expense.setName("Test");
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setUser(user);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(expenseRepository.findByUser(user)).thenReturn(Collections.singletonList(expense));

        var result = expenseService.getExpensesForUser("test@example.com");

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getName());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> expenseService.getExpensesForUser("notfound@example.com"));
    }

    @Test
    void shouldCalculateTotalExpensesCorrectly() {
        User user = new User();
        user.setEmail("test@example.com");

        Expense e1 = new Expense();
        e1.setAmount(BigDecimal.valueOf(100));
        e1.setName("Test1");

        Expense e2 = new Expense();
        e2.setAmount(BigDecimal.valueOf(200));
        e2.setName("Test2");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(expenseRepository.findByUser(user)).thenReturn(Arrays.asList(e1, e2));

        BigDecimal total = expenseService.getTotalForUser("test@example.com");

        assertEquals(BigDecimal.valueOf(300), total);
    }

    @Test
    void shouldThrowIfExpenseHasNullFields() {
        User user = new User();
        user.setEmail("test@example.com");

        Expense e1 = new Expense();
        e1.setAmount(null);
        e1.setName("Test1");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(expenseRepository.findByUser(user)).thenReturn(Collections.singletonList(e1));

        assertThrows(IllegalArgumentException.class,
                () -> expenseService.getTotalForUser("test@example.com"));
    }

    @Test
    void shouldSaveExpenseWithUser() {
        User user = new User();
        user.setEmail("test@example.com");

        Expense expense = new Expense();
        expense.setName("Test");
        expense.setAmount(BigDecimal.valueOf(50));

        expenseService.saveExpense(expense);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());

        Expense savedExpense = captor.getValue();
        assertEquals("Test", savedExpense.getName());
        assertEquals(BigDecimal.valueOf(50), savedExpense.getAmount());
        assertEquals(user, savedExpense.getUser());
    }

    @Test
    void shouldDeleteExpenseIfAuthorized() {
        User user = new User();
        user.setEmail("user@example.com");

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUser(user);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        expenseService.deleteExpenseByIdAndUser(1L, "user@example.com");

        verify(expenseRepository).delete(expense);
    }

    @Test
    void shouldThrowIfDeletingUnauthorizedExpense() {
        User user = new User();
        user.setEmail("owner@example.com");

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUser(user);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThrows(RuntimeException.class,
                () -> expenseService.deleteExpenseByIdAndUser(1L, "other@example.com"));
    }

    @Test
    void shouldThrowIfExpenseToDeleteNotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> expenseService.deleteExpenseByIdAndUser(1L, "user@example.com"));
    }
}