package com.tdb.booking.dto;

import com.tdb.booking.entity.DarshanSlot;
import java.time.LocalDate;
import java.time.LocalTime;

public class SlotResponse {

    private Long id;
    private String templeName;
    private LocalDate date;
    private LocalTime startTime;
    private int capacity;
    private int bookedTickets;
    private int remainingTickets;

    // Constructor
    public SlotResponse(Long id, String templeName, LocalDate date, LocalTime startTime,
                        int capacity, int bookedTickets, int remainingTickets) {
        this.id = id;
        this.templeName = templeName;
        this.date = date;
        this.startTime = startTime;
        this.capacity = capacity;
        this.bookedTickets = bookedTickets;
        this.remainingTickets = remainingTickets;
    }

    // Static factory method
    public static SlotResponse from(DarshanSlot slot) {
        return new SlotResponse(
                slot.getId(),
                slot.getTempleName(),
                slot.getSlotDate(),     // Matches DarshanSlot getter
                slot.getSlotTime(),     // Matches DarshanSlot getter
                slot.getCapacity(),
                slot.getBookedTickets(),
                slot.getRemainingTickets()
        );
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTempleName() {
        return templeName;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getBookedTickets() {
        return bookedTickets;
    }

    public int getRemainingTickets() {
        return remainingTickets;
    }
}