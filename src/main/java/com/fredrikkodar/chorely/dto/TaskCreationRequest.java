package com.fredrikkodar.chorely.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskCreationRequest(@NotBlank(message = "Title must not be blank.")
                                  @Size(min = 8, max = 140, message = "Title must be 8-40 characters.")
                                  String title,
                                  @NotBlank(message = "Description must not be blank.")
                                  @Size(min = 8, max = 255, message = "Description must be 8-255 characters.")
                                  String description,
                                  @NotNull(message = "Points must not be null.")
                                  @Min(value = 0, message = "Points must be a whole number of zero or greater")
                                  Integer points) {


}
