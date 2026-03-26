package com.fredrikkodar.chorely.dto;

import com.fredrikkodar.chorely.enums.TaskStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record TaskUpdateRequest(@Size(min = 8, max = 140, message = "Title must be 8-40 characters.")
                                @Nullable
                                String title,
                                @Size(min = 8, max = 255, message = "Description must be 8-255 characters.")
                                @Nullable
                                String description,
                                @Min(value = 0, message = "Points must be a whole number of zero or greater")
                                @Nullable
                                Integer points,
                                @Nullable
                                TaskStatus status) {

}
