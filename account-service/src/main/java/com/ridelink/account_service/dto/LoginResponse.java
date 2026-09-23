package com.ridelink.account_service.dto;

import com.ridelink.account_service.model.AccountStatus;
import com.ridelink.account_service.model.Role;

public class LoginResponse {

    private String id;
    private String name;
    private String email;
    private Role role;
    private AccountStatus status;
    private String token;

    public LoginResponse(
            String id,
            String name,
            String email,
            Role role,
            AccountStatus status,
            String token) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }
}
