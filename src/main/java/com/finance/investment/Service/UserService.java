package com.finance.investment.Service;

import com.finance.investment.model.User;

import java.util.Optional;

public interface UserService {
    public User registerNewUser(User user);
    public boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}
