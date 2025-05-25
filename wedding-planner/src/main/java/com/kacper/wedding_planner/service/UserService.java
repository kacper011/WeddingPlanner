package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.User;

public interface UserService {

    void registerUser(String email, String password);

    User findByEmail(String email);
}
