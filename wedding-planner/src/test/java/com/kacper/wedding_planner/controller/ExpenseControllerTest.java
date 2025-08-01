package com.kacper.wedding_planner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.service.ExpenseService;
import com.kacper.wedding_planner.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails principal;
    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setEmail("testuser@example.com");
        principal = new CustomUserDetails(testUser);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void shouldShowExpensesViewWithModelAttributes() throws Exception {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setNazwa("Test Expense");
        expense.setKwota(BigDecimal.valueOf(50));

        when(expenseService.getExpensesForUser("testuser@example.com"))
                .thenReturn(List.of(expense));
        when(expenseService.getTotalForUser("testuser@example.com"))
                .thenReturn(BigDecimal.valueOf(50));

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(view().name("expenses"))
                .andExpect(model().attributeExists("expenses"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attributeExists("expense"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void shouldSaveExpenseAndRedirectWhenNoValidationErrors() throws Exception {
        Expense expense = new Expense();
        expense.setNazwa("New Expense");
        expense.setKwota(BigDecimal.valueOf(100));

        when(userService.findByEmail("testuser@example.com")).thenReturn(testUser);

        mockMvc.perform(post("/expenses")
                        .with(csrf())
                        .param("nazwa", "New Expense")
                        .param("kwota", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/expenses"));

        Mockito.verify(expenseService).saveExpense(any(Expense.class), Mockito.eq(testUser));
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void shouldReturnExpensesViewWhenValidationErrors() throws Exception {

        when(expenseService.getExpensesForUser("testuser@example.com"))
                .thenReturn(List.of());
        when(expenseService.getTotalForUser("testuser@example.com"))
                .thenReturn(BigDecimal.ZERO);

        mockMvc.perform(post("/expenses")
                        .with(csrf())
                        .param("name", "")
                        .param("amount", "-10"))
                .andExpect(status().isOk())
                .andExpect(view().name("expenses"))
                .andExpect(model().attributeExists("expenses"))
                .andExpect(model().attributeExists("total"));
    }

}