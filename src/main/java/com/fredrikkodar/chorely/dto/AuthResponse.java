package com.fredrikkodar.chorely.dto;

/// DTO of User to send as response on HTTP request, typically login request.
public class AuthResponse {

    private String token;
    private Long expiresIn;
    private String roles;

    public AuthResponse(String token, Long expiresIn, String roles) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

}
