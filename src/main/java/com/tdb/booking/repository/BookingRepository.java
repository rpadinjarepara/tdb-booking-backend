package com.tdb.booking.repository;

import com.tdb.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdempotencyKey(String idempotencyKey);
    Optional<Booking> findByReceiptNumber(String receiptNumber);
}