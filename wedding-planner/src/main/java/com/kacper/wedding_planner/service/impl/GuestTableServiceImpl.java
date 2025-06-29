package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestTableRepository;
import com.kacper.wedding_planner.service.GuestTableService;

import java.util.List;

public class GuestTableServiceImpl implements GuestTableService {

    private final GuestTableRepository guestTableRepository;

    public GuestTableServiceImpl(GuestTableRepository guestTableRepository) {
        this.guestTableRepository = guestTableRepository;
    }

    @Override
    public List<GuestTable> getTableForUser(User user) {
        return guestTableRepository.findByUser(user);
    }
}
