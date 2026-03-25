package com.fredande.rewardsappbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record ParentUpdateRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName
) {

}
