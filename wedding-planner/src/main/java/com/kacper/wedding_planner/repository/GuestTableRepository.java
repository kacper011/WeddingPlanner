package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestTableRepository extends JpaRepository<GuestTable, Long> {
    List<GuestTable> findByUser(User user);
}
