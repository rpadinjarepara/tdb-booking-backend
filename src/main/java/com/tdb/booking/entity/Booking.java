package com.tdb.booking.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false)
    private DarshanSlot slot;

    @Column(nullable = false)
    private String devoteeName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private int ticketCount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Booking() {
    }

    public Booking(DarshanSlot slot, String devoteeName,
                   String phoneNumber, int ticketCount) {
        this.slot = slot;
        this.devoteeName = devoteeName;
        this.phoneNumber = phoneNumber;
        this.ticketCount = ticketCount;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public DarshanSlot getSlot() {
        return slot;
    }

    public String getDevoteeName() {
        return devoteeName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}