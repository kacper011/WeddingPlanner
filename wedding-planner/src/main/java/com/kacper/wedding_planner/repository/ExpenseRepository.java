package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
}
