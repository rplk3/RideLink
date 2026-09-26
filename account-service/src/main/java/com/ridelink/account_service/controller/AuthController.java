package com.ridelink.account_service.controller;

import com.ridelink.account_service.dto.LoginRequest;
import com.ridelink.account_service.dto.LoginResponse;
import com.ridelink.account_service.dto.RegisterRequest;
import com.ridelink.account_service.dto.RegisterResponse;
import com.ridelink.account_service.model.User;
import com.ridelink.account_service.security.JwtService;
import com.ridelink.account_service.service.AccountService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final AccountService accountService;
        private final JwtService jwtService;

        public AuthController(AccountService accountService, JwtService jwtService) {
                this.accountService = accountService;
                this.jwtService = jwtService;
        }

        @PostMapping("/register")
        public ResponseEntity<RegisterResponse> register(
                        @Valid @RequestBody RegisterRequest request) {

                User user = accountService.registerUser(request);

                RegisterResponse response = new RegisterResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole(),
                                user.getStatus());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> login(
                        @Valid @RequestBody LoginRequest request) {

                User user = accountService.authenticateUser(
                                request.getEmail(),
                                request.getPassword());

                String token = jwtService.generateToken(
                                user.getId(),
                                user.getEmail(),
                                user.getRole().name());

                LoginResponse response = new LoginResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole(),
                                user.getStatus(),
                                token);

                return ResponseEntity.ok(response);
        }


        @GetMapping("/me")
        public ResponseEntity<String> getCurrentUser(
                        org.springframework.security.core.Authentication authentication) {

                String userId = authentication.getName();

                return ResponseEntity.ok("Authenticated user ID: " + userId);
        }
}
