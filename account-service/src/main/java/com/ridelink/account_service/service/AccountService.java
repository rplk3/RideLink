package com.ridelink.account_service.service;

import com.ridelink.account_service.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final UserRepository userRepository;

    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}