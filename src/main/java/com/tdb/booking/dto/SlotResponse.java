package com.tdb.booking.dto;

import com.tdb.booking.entity.DarshanSlot;
import java.time.LocalDate;
import java.time.LocalTime;

public record SlotResponse(
        Long id,
        String templeName,
        LocalDate date,
        LocalTime startTime,
        int capacity,
        int bookedTickets,
        int remainingTickets
) {
    public static SlotResponse from(DarshanSlot slot) {
        return new SlotResponse(
                slot.getId(),
                slot.getTempleName(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getCapacity(),
                slot.getBookedTickets(),
                slot.getRemainingTickets()
        );
    }
}