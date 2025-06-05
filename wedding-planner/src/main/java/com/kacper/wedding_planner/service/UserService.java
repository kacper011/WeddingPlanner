package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.User;

public interface UserService {

    void registerUser(String email, String password, String firstName);

    User findByEmail(String email);
}
