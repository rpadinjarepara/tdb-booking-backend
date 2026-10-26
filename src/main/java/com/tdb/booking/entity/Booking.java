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

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @Column(nullable = false, unique = true)
    private String receiptNumber;

    @Column(nullable = false)
    private boolean cancelled = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Booking() {
        // JPA protected no-args constructor
    }

    // 6-parameter constructor used in BookingService
    public Booking(
            DarshanSlot slot,
            String devoteeName,
            String phoneNumber,
            Integer ticketCount,
            String idempotencyKey,
            String receiptNumber
    ) {
        this.slot = slot;
        this.devoteeName = devoteeName;
        this.phoneNumber = phoneNumber;
        this.ticketCount = ticketCount;
        this.idempotencyKey = idempotencyKey;
        this.receiptNumber = receiptNumber;
        this.createdAt = LocalDateTime.now();
    }

    public void cancel() {
        this.cancelled = true;
    }

    // Getters
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

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}