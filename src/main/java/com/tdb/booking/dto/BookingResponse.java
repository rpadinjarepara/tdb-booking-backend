package com.tdb.booking.dto;

import com.tdb.booking.entity.Booking;
import java.time.Instant;

public record BookingResponse(
        Long id,
        Long slotId,
        String devoteeName,
        String phoneNumber,
        int ticketCount,
        Instant createdAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getSlot().getId(),
                booking.getDevoteeName(),
                booking.getPhoneNumber(),
                booking.getTicketCount(),
                booking.getCreatedAt()
        );
    }
}