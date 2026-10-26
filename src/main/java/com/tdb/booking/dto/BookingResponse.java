package com.tdb.booking.dto;

import com.tdb.booking.entity.Booking;

public record BookingResponse(
        Long id,
        Long slotId,
        String devoteeName,
        String phoneNumber,
        int ticketCount,
        java.time.LocalDateTime createdAt
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