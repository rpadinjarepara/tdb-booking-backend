package com.tdb.booking.controller;

import com.tdb.booking.dto.*;
import com.tdb.booking.service.BookingService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        log.info("Received request to create booking for slotId: {}, devotee: {}, idempotencyKey: {}",
                request.slotId(), request.devoteeName(), request.idempotencyKey());

        BookingResponse response = bookingService.createBooking(request);

        log.info("Successfully processed booking. ID: {}, Receipt: {}",
                response.id(), response.receiptNumber());
        return response;
    }

    @GetMapping("/{id}")
    public BookingResponse getBooking(@PathVariable Long id) {
        log.debug("Fetching booking details for ID: {}", id);
        return bookingService.getBooking(id);
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable Long id) {
        log.info("Received request to cancel booking ID: {}", id);

        BookingResponse response = bookingService.cancelBooking(id);

        log.info("Successfully cancelled booking ID: {}", id);
        return response;
    }

    @GetMapping("/receipt/{receiptNumber}")
    public BookingResponse getBookingByReceipt(@PathVariable String receiptNumber) {
        log.debug("Fetching booking details for receipt: {}", receiptNumber);
        return bookingService.getBookingByReceipt(receiptNumber);
    }

}