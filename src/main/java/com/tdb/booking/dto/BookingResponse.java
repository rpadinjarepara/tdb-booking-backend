package com.tdb.booking.dto;

import com.tdb.booking.entity.Booking;

public record BookingResponse(
        Long id,
        Long slotId,
        String templeName,
        String devoteeName,
        String phoneNumber,
        Integer ticketCount,
        boolean cancelled
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getSlot().getId(),
                booking.getSlot().getTempleName(),
                booking.getDevoteeName(),
                booking.getPhoneNumber(),
                booking.getTicketCount(),
                booking.isCancelled()
        );
    }
}