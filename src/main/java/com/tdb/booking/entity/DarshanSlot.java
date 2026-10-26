package com.tdb.booking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "darshan_slots")
public class DarshanSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templeName;
    private LocalDate slotDate;
    private LocalTime slotTime;
    private int capacity;         // Total maximum capacity
    private int bookedTickets = 0; // Tickets booked so far

    // No-args constructor required by JPA
    public DarshanSlot() {
    }

    // 4-args constructor matching BookingService
    public DarshanSlot(String templeName, LocalDate slotDate, LocalTime slotTime, int capacity) {
        this.templeName = templeName;
        this.slotDate = slotDate;
        this.slotTime = slotTime;
        this.capacity = capacity;
        this.bookedTickets = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTempleName() {
        return templeName;
    }

    public void setTempleName(String templeName) {
        this.templeName = templeName;
    }

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(LocalDate slotDate) {
        this.slotDate = slotDate;
    }

    public LocalTime getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(LocalTime slotTime) {
        this.slotTime = slotTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getBookedTickets() {
        return bookedTickets;
    }

    public void setBookedTickets(int bookedTickets) {
        this.bookedTickets = bookedTickets;
    }

    // Helper methods used in BookingService and SlotResponse
    public int getRemainingTickets() {
        return capacity - bookedTickets;
    }

    public void addBooking(int ticketCount) {
        this.bookedTickets += ticketCount;
    }
}