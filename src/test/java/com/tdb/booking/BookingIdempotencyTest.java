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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingIdempotencyTest {

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
    void testIdempotentBookingRetry() throws Exception {
        // 1. Create a darshan slot
        CreateSlotRequest slotRequest = new CreateSlotRequest(
                "Tirupati",
                LocalDate.now().plusDays(3),
                LocalTime.of(9, 30),
                20
        );

        mockMvc.perform(post("/api/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotRequest)))
                .andExpect(status().isCreated());

        Long slotId = 1L;

        // 2. Prepare a booking request with a unique idempotency key
        CreateBookingRequest bookingRequest = new CreateBookingRequest(
                slotId,
                "Rahul Sharma",
                "9876543210",
                2,
                "idemp-key-uuid-987654"
        );

        // 3. First call: Should create a new booking (HTTP 201)
        var firstResult = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // 4. Second call: Simulated network retry with the exact same idempotency key
        var secondResult = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // 5. Verify both responses are identical and no duplicate booking was created in the database
        String firstResponseBody = firstResult.getResponse().getContentAsString();
        String secondResponseBody = secondResult.getResponse().getContentAsString();

        assertEquals(firstResponseBody, secondResponseBody, "Retried request must return the exact same response body");
        assertEquals(1, bookingRepository.count(), "Idempotent retries must not create duplicate database records");

        // 6. Verify slot ticket count only reflects a single deduction (2 tickets)
        var slot = slotRepository.findById(slotId).orElseThrow();
        assertEquals(2, slot.getBookedTickets(), "Slot booked tickets should only increase once");
    }
}