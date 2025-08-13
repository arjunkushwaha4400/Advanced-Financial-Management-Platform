package com.finance.investment.Service;

import com.finance.investment.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.finance.investment.model.User;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUser(User user) {
        // Securely hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign the default USER role
        user.setRoles(Collections.singleton("ROLE_USER"));

        return userRepository.save(user);
    }
    @Override
    public boolean existsByUsername(String username) {
        // This method correctly links the service to the repository.
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
