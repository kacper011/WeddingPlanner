package com.kacper.wedding_planner.mapper;

import com.kacper.wedding_planner.dto.ExpenseRequest;
import com.kacper.wedding_planner.model.Expense;
import com.kacper.wedding_planner.model.User;

public class ExpenseMapper {

    public static Expense toEntity(ExpenseRequest request, User user) {
        Expense e = new Expense();
        e.setName(request.getName());
        e.setAmount(request.getAmount());
        e.setUser(user);
        return e;
    }
}
