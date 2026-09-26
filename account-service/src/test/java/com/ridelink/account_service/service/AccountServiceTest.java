package com.ridelink.account_service.service;

import com.ridelink.account_service.dto.RegisterRequest;
import com.ridelink.account_service.exception.DuplicateEmailException;
import com.ridelink.account_service.exception.InvalidCredentialsException;
import com.ridelink.account_service.exception.SuspendedAccountException;
import com.ridelink.account_service.model.AccountStatus;
import com.ridelink.account_service.model.Role;
import com.ridelink.account_service.model.User;
import com.ridelink.account_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123");
        registerRequest.setRole(Role.PASSENGER);
    }

    @Test
    void registerUser_shouldCreateActiveUser() {

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123"))
                .thenReturn("hashedPassword");

        User savedUser = new User();
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(Role.PASSENGER);
        savedUser.setStatus(AccountStatus.ACTIVE);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = accountService.registerUser(registerRequest);

        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("hashedPassword", result.getPassword());
        assertEquals(Role.PASSENGER, result.getRole());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Password123");
    }

    @Test
    void registerUser_shouldRejectDuplicateEmail() {

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> accountService.registerUser(registerRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_shouldReturnUserForValidCredentials() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Password123", "hashedPassword"))
                .thenReturn(true);

        User result = accountService.authenticateUser(
                "test@example.com",
                "Password123"
        );

        assertSame(user, result);
    }

    @Test
    void authenticateUser_shouldRejectInvalidPassword() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("WrongPassword", "hashedPassword"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> accountService.authenticateUser(
                        "test@example.com",
                        "WrongPassword"
                )
        );
    }

    @Test
    void authenticateUser_shouldRejectUnknownEmail() {

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> accountService.authenticateUser(
                        "unknown@example.com",
                        "Password123"
                )
        );
    }

    @Test
    void authenticateUser_shouldRejectSuspendedAccount() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setStatus(AccountStatus.SUSPENDED);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Password123", "hashedPassword"))
                .thenReturn(true);

        assertThrows(
                SuspendedAccountException.class,
                () -> accountService.authenticateUser(
                        "test@example.com",
                        "Password123"
                )
        );
    }
}