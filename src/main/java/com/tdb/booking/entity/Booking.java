package com.tdb.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private DarshanSlot slot;

    @Column(nullable = false)
    private String devoteeName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer ticketCount;

    @Column(unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Added timestamp field

    protected Booking() {}

    public Booking(
            DarshanSlot slot,
            String devoteeName,
            String phoneNumber,
            Integer ticketCount,
            String idempotencyKey
    ) {
        this.slot = slot;
        this.devoteeName = devoteeName;
        this.phoneNumber = phoneNumber;
        this.ticketCount = ticketCount;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = LocalDateTime.now();
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

    public Integer getTicketCount() {
        return ticketCount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public LocalDateTime getCreatedAt() { // Added getter method
        return createdAt;
    }

}