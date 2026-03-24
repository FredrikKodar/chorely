package com.fredande.rewardsappbackend.dto;

import jakarta.validation.constraints.*;

public class ParentRequest {

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
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    public ParentRequest() {
    }

    public ParentRequest(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
    return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

public ParentRequest withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ParentRequest withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

}
