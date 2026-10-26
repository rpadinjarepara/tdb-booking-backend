package com.tdb.booking.exception;

public class BookingCapacityException extends RuntimeException {
    public BookingCapacityException(String message) {
        super(message);
    }
}