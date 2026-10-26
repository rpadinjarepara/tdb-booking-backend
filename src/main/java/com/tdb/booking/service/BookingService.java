package com.tdb.booking.service;

import com.tdb.booking.dto.*;
import com.tdb.booking.entity.*;
import com.tdb.booking.exception.*;
import com.tdb.booking.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;

    public BookingService(
            SlotRepository slotRepository,
            BookingRepository bookingRepository) {
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public SlotResponse createSlot(CreateSlotRequest request) {
        log.info("Creating darshan slot for temple: {}, date: {}, startTime: {}, capacity: {}",
                request.templeName(), request.date(), request.startTime(), request.capacity());

        DarshanSlot slot = new DarshanSlot(
                request.templeName().trim(),
                request.date(),
                request.startTime(),
                request.capacity()
        );

        DarshanSlot savedSlot = slotRepository.save(slot);
        log.info("Successfully created slot with ID: {}", savedSlot.getId());
        return SlotResponse.from(savedSlot);
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Processing booking request for slotId: {}, devotee: {}, tickets: {}, idempotencyKey: {}",
                request.slotId(), request.devoteeName(), request.ticketCount(), request.idempotencyKey());

        // P3: Safe Retries / Idempotency Check
        if (request.idempotencyKey() != null && !request.idempotencyKey().isBlank()) {
            var existingBooking = bookingRepository.findByIdempotencyKey(request.idempotencyKey());
            if (existingBooking.isPresent()) {
                log.info("Idempotency key hit: {}. Returning existing booking ID: {}",
                        request.idempotencyKey(), existingBooking.get().getId());
                return BookingResponse.from(existingBooking.get());
            }
        }

        // Lock this slot row while checking and updating capacity.
        DarshanSlot slot = slotRepository
                .findByIdForUpdate(request.slotId())
                .orElseThrow(() -> {
                    log.warn("Slot not found for ID: {}", request.slotId());
                    return new ResourceNotFoundException("Slot not found: " + request.slotId());
                });

        if (request.ticketCount() > slot.getRemainingTickets()) {
            log.warn("Booking capacity exceeded for slot ID: {}. Requested: {}, Available: {}",
                    request.slotId(), request.ticketCount(), slot.getRemainingTickets());
            throw new BookingCapacityException(
                    "Only " + slot.getRemainingTickets() + " tickets are available");
        }

        slot.addBooking(request.ticketCount());
        slotRepository.save(slot);

        // P5: Generate unique receipt number
        String receiptNumber = "TDB-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = new Booking(
                slot,
                request.devoteeName().trim(),
                request.phoneNumber(),
                request.ticketCount(),
                request.idempotencyKey(),
                receiptNumber
        );

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Successfully created booking ID: {} with receipt: {} for slot ID: {}",
                savedBooking.getId(), savedBooking.getReceiptNumber(), request.slotId());
        return BookingResponse.from(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id) {
        log.debug("Fetching booking by ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Booking not found for ID: {}", id);
                    return new ResourceNotFoundException("Booking not found: " + id);
                });

        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingByReceipt(String receiptNumber) {
        log.debug("Fetching booking by receipt number: {}", receiptNumber);
        Booking booking = bookingRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> {
                    log.warn("Booking not found for receipt number: {}", receiptNumber);
                    return new ResourceNotFoundException("Booking not found with receipt: " + receiptNumber);
                });

        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public SlotResponse getSlot(Long id) {
        log.debug("Fetching slot by ID: {}", id);
        DarshanSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Slot not found for ID: {}", id);
                    return new ResourceNotFoundException("Slot not found: " + id);
                });

        return SlotResponse.from(slot);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        log.info("Processing cancellation request for booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found for cancellation, ID: {}", bookingId);
                    return new ResourceNotFoundException("Booking not found: " + bookingId);
                });

        // Idempotent cancellation: if already cancelled, return current state
        if (booking.isCancelled()) {
            log.info("Booking ID: {} is already cancelled. Returning current state.", bookingId);
            return BookingResponse.from(booking);
        }

        DarshanSlot slot = slotRepository.findByIdForUpdate(booking.getSlot().getId())
                .orElseThrow(() -> {
                    log.warn("Associated slot not found for booking ID: {}", bookingId);
                    return new ResourceNotFoundException("Associated slot not found");
                });

        // Verify slot start time has not passed
        var slotDateTime = java.time.LocalDateTime.of(slot.getSlotDate(), slot.getSlotTime());
        if (java.time.LocalDateTime.now().isAfter(slotDateTime)) {
            log.warn("Attempted to cancel past booking ID: {} where slot start time was: {}", bookingId, slotDateTime);
            throw new IllegalStateException("Cannot cancel booking after the slot start time has passed");
        }

        slot.releaseBooking(booking.getTicketCount());
        slotRepository.save(slot);

        booking.cancel();
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Successfully cancelled booking ID: {} and restored {} tickets to slot ID: {}",
                bookingId, booking.getTicketCount(), slot.getId());
        return BookingResponse.from(savedBooking);
    }
}