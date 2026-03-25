package com.fredande.rewardsappbackend.dto;

import com.fredande.rewardsappbackend.constants.ValidationConstants;
import jakarta.validation.constraints.*;

public record ParentRegistrationRequest(
        @NotNull
        @Pattern(regexp = ValidationConstants.EMAIL_REGEX,
                message = "Malformed email address")
        String email,

        @NotNull
        @Size(min = ValidationConstants.MIN_PASSWORD_LENGTH,
                max = ValidationConstants.MAX_PASSWORD_LENGTH,
                message = "Password must be " + ValidationConstants.MIN_PASSWORD_LENGTH + " to " + ValidationConstants.MAX_PASSWORD_LENGTH + " characters")
        String password,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName
) {

}
