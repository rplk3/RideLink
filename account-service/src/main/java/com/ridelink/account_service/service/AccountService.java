package com.ridelink.account_service.service;

import com.ridelink.account_service.dto.RegisterRequest;
import com.ridelink.account_service.model.AccountStatus;
import com.ridelink.account_service.model.User;
import com.ridelink.account_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        String hashedPassword = passwordEncoder.encode(
                request.getPassword()
        );

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        user.setRole(request.getRole());
        user.setStatus(AccountStatus.ACTIVE);

        return userRepository.save(user);
    }
    public User authenticateUser(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (user.getStatus() == AccountStatus.SUSPENDED) {
        throw new IllegalArgumentException("Account is suspended");
    }

        return user;
    }
}