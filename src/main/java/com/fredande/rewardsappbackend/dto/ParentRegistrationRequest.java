package com.fredande.rewardsappbackend.dto;

import jakarta.validation.constraints.*;

public class ParentRegistrationRequest {

    private final int minPasswordLength = 8;
    private final int maxPasswordLength = 40;
    private final String emailRegEx = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


    @Pattern(regexp = emailRegEx,
            message = "Malformed email address")
    private String email;
    @Size(min = minPasswordLength,
            max = maxPasswordLength,
            message = "Password must be " + minPasswordLength + " to " + maxPasswordLength + " characters")
    private String password;

    public ParentRegistrationRequest() {
    }

    public ParentRegistrationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
