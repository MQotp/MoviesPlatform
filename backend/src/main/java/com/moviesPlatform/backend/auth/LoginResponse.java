package com.moviesPlatform.backend.auth;

import java.util.Date;

public class LoginResponse {
    private String username;
    private String token;
    private String role;
    private Date expiresAt;

    public LoginResponse() {
    }

    public LoginResponse(String username, String token, String role, Date expiresAt) {
        this.username = username;
        this.token = token;
        this.role = role;
        this.expiresAt = expiresAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
