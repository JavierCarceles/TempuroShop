package com.tempuro.auth.service;

import com.tempuro.auth.model.User;

public interface UserServiceInterface {
    public User findByEmail(String email);
}