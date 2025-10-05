package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;

import java.util.List;

public interface GuestTableService {

    List<GuestTable> getTablesForUser(User user);
    void deleteTableById(Long id);
}
