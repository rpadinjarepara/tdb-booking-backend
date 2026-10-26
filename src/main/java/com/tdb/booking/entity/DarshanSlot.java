package com.tdb.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "darshan_slots")
public class DarshanSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String templeName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int bookedTickets = 0;

    protected DarshanSlot() {
    }

    public DarshanSlot(String templeName, LocalDate date,
                       LocalTime startTime, int capacity) {
        this.templeName = templeName;
        this.date = date;
        this.startTime = startTime;
        this.capacity = capacity;
    }

    public int getRemainingTickets() {
        return capacity - bookedTickets;
    }

    public void addBooking(int ticketCount) {
        if (ticketCount > getRemainingTickets()) {
            throw new IllegalArgumentException(
                    "Not enough tickets available");
        }
        bookedTickets += ticketCount;
    }

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
}