package com.cinema.controller;

import com.cinema.model.Seat;
import com.cinema.service.SeatService;
import com.cinema.service.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;
    private final ShowtimeService showtimeService;

    public SeatController(SeatService seatService, ShowtimeService showtimeService) {
        this.seatService = seatService;
        this.showtimeService = showtimeService;
    }

    /** All seats in a theater */
    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<Seat>> getSeatsByTheater(@PathVariable Long theaterId) {
        return ResponseEntity.ok(seatService.getSeatsByTheater(theaterId));
    }

    /** Available (unbooked) seats for a given showtime */
    @GetMapping("/showtime/{showtimeId}/available")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long showtimeId) {
        Long theaterId = showtimeService.getShowtimeById(showtimeId).getTheaterId();
        return ResponseEntity.ok(seatService.getAvailableSeats(showtimeId, theaterId));
    }

    /** Booked seats for a given showtime */
    @GetMapping("/showtime/{showtimeId}/booked")
    public ResponseEntity<List<Seat>> getBookedSeats(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(seatService.getBookedSeats(showtimeId));
    }
}
