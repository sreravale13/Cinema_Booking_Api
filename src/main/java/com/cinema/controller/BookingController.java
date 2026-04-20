package com.cinema.controller;

import com.cinema.model.Booking;
import com.cinema.service.BookingService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ── Inner DTO ─────────────────────────────────────────────────────────────
    public static class BookingRequest {
        @NotNull(message = "customerId is required")
        public Long customerId;

        @NotNull(message = "showtimeId is required")
        public Long showtimeId;

        @NotEmpty(message = "At least one seat must be selected")
        public List<Long> seatIds;
    }

    // ── Endpoints ─────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/reference/{ref}")
    public ResponseEntity<Booking> getBookingByReference(@PathVariable String ref) {
        return ResponseEntity.ok(bookingService.getBookingByReference(ref));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Booking>> getBookingsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(bookingService.getBookingsByCustomer(customerId));
    }

    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<List<Booking>> getBookingsByShowtime(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(bookingService.getBookingsByShowtime(showtimeId));
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(
                request.customerId, request.showtimeId, request.seatIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<Booking> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.confirmPayment(id));
    }
}
