package com.tdb.booking.dto;

import jakarta.validation.constraints.*;

public record CreateBookingRequest(

        @NotNull(message = "Slot ID is required")
        @Positive(message = "Slot ID must be positive")
        Long slotId,

        @NotBlank(message = "Devotee name is required")
        String devoteeName,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[0-9]{10,15}$",
                message = "Phone number must contain 10 to 15 digits"
        )
        String phoneNumber,

        @NotNull(message = "Ticket count is required")
        @Min(value = 1, message = "At least one ticket must be booked")
        Integer ticketCount,

        String idempotencyKey // P3: Safe Retries / Idempotency Key
) {
}