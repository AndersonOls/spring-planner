package com.anderson.planner.trip;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jdk.jfr.Name;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

public record TripRequestPayload(@NotEmpty(message = "Destination cannot be empty") String destination,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String starts_at,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String ends_at,
                                 @NotEmpty(message = "Email cannot be empty") List<@Email(message = "Invalid email") String> emails_to_invite,
                                 @Email(message = "Invalid email") @NotEmpty(message = "Email cannot be empty") String owner_email,
                                 @NotEmpty(message = "Name cannot be empty") String owner_name) {
}
