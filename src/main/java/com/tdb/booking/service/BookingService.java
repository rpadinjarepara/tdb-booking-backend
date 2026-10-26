package com.tdb.booking.service;

import com.tdb.booking.dto.*;
import com.tdb.booking.entity.*;
import com.tdb.booking.exception.*;
import com.tdb.booking.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

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
        DarshanSlot slot = new DarshanSlot(
                request.templeName().trim(),
                request.date(),
                request.startTime(),
                request.capacity()
        );

        return SlotResponse.from(slotRepository.save(slot));
    }

    @Transactional
    public BookingResponse createBooking(
            CreateBookingRequest request) {

        // Lock this slot row while checking and updating capacity.
        DarshanSlot slot = slotRepository
                .findByIdForUpdate(request.slotId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Slot not found: " + request.slotId()));

        if (request.ticketCount() > slot.getRemainingTickets()) {
            throw new BookingCapacityException(
                    "Only " + slot.getRemainingTickets()
                            + " tickets are available");
        }

        slot.addBooking(request.ticketCount());

        Booking booking = new Booking(
                slot,
                request.devoteeName().trim(),
                request.phoneNumber(),
                request.ticketCount()
        );

        // Both operations participate in the same transaction.
        return BookingResponse.from(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found: " + id));

        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public SlotResponse getSlot(Long id) {
        DarshanSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Slot not found: " + id));

        return SlotResponse.from(slot);
    }
}