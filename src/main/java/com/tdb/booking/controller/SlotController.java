package com.tdb.booking.controller;

import com.tdb.booking.dto.*;
import com.tdb.booking.service.BookingService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final BookingService bookingService;

    public SlotController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlotResponse createSlot(
            @Valid @RequestBody CreateSlotRequest request) {
        return bookingService.createSlot(request);
    }

    @GetMapping("/{id}")
    public SlotResponse getSlot(@PathVariable Long id) {
        return bookingService.getSlot(id);
    }
}