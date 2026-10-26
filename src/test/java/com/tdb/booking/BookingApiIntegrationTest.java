package com.tdb.booking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldBookTicketsAndRejectOverCapacity() throws Exception {

        // Create a slot with capacity 10.
        MvcResult slotResult = mockMvc.perform(post("/api/slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "templeName": "Sabarimala",
                      "date": "2026-12-15",
                      "startTime": "09:00:00",
                      "capacity": 10
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.capacity").value(10))
                .andExpect(jsonPath("$.remainingTickets").value(10))
                .andReturn();

        JsonNode slotJson = objectMapper.readTree(
                slotResult.getResponse().getContentAsString());

        long slotId = slotJson.get("id").asLong();

        // Book 3 tickets.
        MvcResult bookingResult = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "slotId": %d,
                      "devoteeName": "Rahul",
                      "phoneNumber": "9876543210",
                      "ticketCount": 3
                    }
                    """.formatted(slotId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketCount").value(3))
                .andReturn();

        long bookingId = objectMapper.readTree(
                        bookingResult.getResponse().getContentAsString())
                .get("id").asLong();

        // Verify booking retrieval.
        mockMvc.perform(get("/api/bookings/" + bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.devoteeName").value("Rahul"))
                .andExpect(jsonPath("$.ticketCount").value(3));

        // Capacity should now be 7.
        mockMvc.perform(get("/api/slots/" + slotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingTickets").value(7));

        // Only 7 tickets remain; requesting 8 must fail.
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "slotId": %d,
                      "devoteeName": "Anu",
                      "phoneNumber": "9876543211",
                      "ticketCount": 8
                    }
                    """.formatted(slotId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/bookings"));

        // Invalid ticket count must use the same error structure.
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "slotId": %d,
                      "devoteeName": "Anu",
                      "phoneNumber": "9876543211",
                      "ticketCount": 0
                    }
                    """.formatted(slotId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/bookings"));
    }
}