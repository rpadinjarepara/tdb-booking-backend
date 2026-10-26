package com.tdb.booking.repository;

import com.tdb.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // P3: Used for idempotency check during safe retries
    Optional<Booking> findByIdempotencyKey(String idempotencyKey);

    // P5: Used for gate verification and receipt lookups
    Optional<Booking> findByReceiptNumber(String receiptNumber);
}