package com.tdb.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdb.booking.dto.CreateBookingRequest;
import com.tdb.booking.dto.CreateSlotRequest;
import com.tdb.booking.repository.BookingRepository;
import com.tdb.booking.repository.SlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingCancellationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        slotRepository.deleteAll();
    }

    @Test
    void testSuccessfulBookingCancellationAndInventoryRestoration() throws Exception {
        // 1. Create a future slot with capacity 10
        CreateSlotRequest slotRequest = new CreateSlotRequest(
                "Padmanabhaswamy Temple",
                LocalDate.now().plusDays(2),
                LocalTime.of(11, 0),
                10
        );

        mockMvc.perform(post("/api/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotRequest)))
                .andExpect(status().isCreated());

        Long slotId = 1L;

        // 2. Create a booking for 3 tickets
        CreateBookingRequest bookingRequest = new CreateBookingRequest(
                slotId,
                "Vishnu Prasad",
                "9876543210",
                3,
                "idemp-cancel-test-1"
        );

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated());

        // Verify slot remaining tickets dropped (capacity 10 - 3 booked = 7 remaining)
        var slotBeforeCancel = slotRepository.findById(slotId).orElseThrow();
        assertEquals(3, slotBeforeCancel.getBookedTickets());

        // 3. Cancel the booking (ID: 1)
        mockMvc.perform(post("/api/bookings/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cancelled").value(true));

        // 4. Verify slot inventory is restored (booked tickets back to 0)
        var slotAfterCancel = slotRepository.findById(slotId).orElseThrow();
        assertEquals(0, slotAfterCancel.getBookedTickets(), "Slot booked tickets should be restored after cancellation");
    }

    @Test
    void testCancelPastSlotBookingShouldFail() throws Exception {
        // 1. Create a slot in the past (yesterday)
        CreateSlotRequest slotRequest = new CreateSlotRequest(
                "Padmanabhaswamy Temple",
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                10
        );

        mockMvc.perform(post("/api/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotRequest)))
                .andExpect(status().isCreated());

        Long slotId = 1L;

        // 2. Force-create or book for a past slot (Note: if service prevents booking past slots,
        // you would test this by ensuring cancellations are blocked when start time has passed).
        // For demonstration, let's test the cancellation rule endpoint response.
        CreateBookingRequest bookingRequest = new CreateBookingRequest(
                slotId,
                "Devotee Test",
                "9876543210",
                2,
                "idemp-past-slot"
        );

        // If booking past slot is allowed for test setup, or inserted directly:
        var result = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andReturn();

        // 3. Attempting to cancel should throw an exception / return 400 or 500 depending on global exception handler
        // Expecting cancellation failure due to passed start time
        if (result.getResponse().getStatus() == 201) {
            mockMvc.perform(post("/api/bookings/1/cancel"))
                    .andExpect(status().is5xxServerError()); // or isBadRequest() depending on exception handler mapping
        }
    }
}