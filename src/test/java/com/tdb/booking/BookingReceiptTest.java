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

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingReceiptTest {

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
    void testBookingGenerationAndReceiptLookup() throws Exception {
        // 1. Create a darshan slot
        CreateSlotRequest slotRequest = new CreateSlotRequest(
                "Guruvayur Temple",
                LocalDate.now().plusDays(5),
                LocalTime.of(8, 0),
                15
        );

        mockMvc.perform(post("/api/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(slotRequest)))
                .andExpect(status().isCreated());

        Long slotId = 1L;

        // 2. Create a booking request
        CreateBookingRequest bookingRequest = new CreateBookingRequest(
                slotId,
                "Ramesh Kumar",
                "9123456780",
                2,
                "idemp-receipt-test-1"
        );

        // 3. Verify creation returns a valid receipt number starting with "TDB-"
        var result = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.receiptNumber").exists())
                .andExpect(jsonPath("$.receiptNumber", startsWith("TDB-")))
                .andReturn();

        // Extract receipt number from response
        String responseBody = result.getResponse().getContentAsString();
        com.jayway.jsonpath.DocumentContext jsonContext = com.jayway.jsonpath.JsonPath.parse(responseBody);
        String receiptNumber = jsonContext.read("$.receiptNumber");

        // 4. Test GET /api/bookings/receipt/{receiptNumber} endpoint
        mockMvc.perform(get("/api/bookings/receipt/" + receiptNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiptNumber").value(receiptNumber))
                .andExpect(jsonPath("$.devoteeName").value("Ramesh Kumar"))
                .andExpect(jsonPath("$.ticketCount").value(2));
    }
}