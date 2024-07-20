package com.anderson.planner.activities;

import jakarta.validation.constraints.NotEmpty;

public record ActivityRequestPayload(@NotEmpty(message = "Title cannot be empty") String title,
                                     @NotEmpty(message = "Occurs at cannot be empty") String occurs_at) {
}
