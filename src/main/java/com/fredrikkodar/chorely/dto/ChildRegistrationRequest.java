package com.fredrikkodar.chorely.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ChildRegistrationRequest {

    private String username;
    @Pattern(regexp = "^[0-9]*$", message = "Passcode must only contain numbers.")
    @Size(min = 8, max = 20, message = "Passcode must be 8-20 characters.")
    private String password;
    private String firstName;

    public ChildRegistrationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

}
