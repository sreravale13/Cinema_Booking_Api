package com.cinema;

import com.cinema.model.Booking;
import com.cinema.model.Movie;
import com.cinema.model.Seat;
import com.cinema.model.Showtime;
import com.cinema.service.BookingService;
import com.cinema.service.MovieService;
import com.cinema.service.SeatService;
import com.cinema.service.ShowtimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CinemaBookingIntegrationTest {

    @Autowired MovieService    movieService;
    @Autowired ShowtimeService showtimeService;
    @Autowired SeatService     seatService;
    @Autowired BookingService  bookingService;

    @Test
    void contextLoads() {
        // Verifies the Spring context starts without errors
    }

    @Test
    void shouldLoadSeedMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertThat(movies).isNotEmpty();
        assertThat(movies).anyMatch(m -> m.getTitle().equals("Inception"));
    }

    @Test
    void shouldLoadSeedShowtimes() {
        List<Showtime> showtimes = showtimeService.getAllShowtimes();
        assertThat(showtimes).isNotEmpty();
    }

    @Test
    void shouldListAvailableSeatsForShowtime() {
        // Showtime 1 is Hall 1 (80 seats) — none booked yet
        List<Seat> available = seatService.getAvailableSeats(1L, 1L);
        assertThat(available).hasSize(80);
    }

    @Test
    void shouldCreateAndCancelBooking() {
        // Pick first two available seats for showtime 1
        List<Seat> seats = seatService.getAvailableSeats(1L, 1L);
        List<Long> seatIds = seats.stream().map(Seat::getId).limit(2).toList();

        // Customer 1 books 2 seats
        Booking booking = bookingService.createBooking(1L, 1L, seatIds);
        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getBookingReference()).startsWith("BKG-");
        assertThat(booking.getNumberOfTickets()).isEqualTo(2);
        assertThat(booking.getStatus()).isEqualTo("CONFIRMED");
        assertThat(booking.getSeatNumbers()).hasSize(2);

        // Available seats should decrease
        Showtime updated = showtimeService.getShowtimeById(1L);
        assertThat(updated.getAvailableSeats()).isEqualTo(78);

        // Cancel the booking
        Booking cancelled = bookingService.cancelBooking(booking.getId());
        assertThat(cancelled.getStatus()).isEqualTo("CANCELLED");
        assertThat(cancelled.getPaymentStatus()).isEqualTo("REFUNDED");

        // Available seats should be restored
        Showtime restored = showtimeService.getShowtimeById(1L);
        assertThat(restored.getAvailableSeats()).isEqualTo(80);
    }

    @Test
    void shouldConfirmPayment() {
        List<Seat> seats = seatService.getAvailableSeats(1L, 1L);
        List<Long> seatIds = seats.stream().map(Seat::getId).limit(1).toList();

        Booking booking = bookingService.createBooking(1L, 1L, seatIds);
        assertThat(booking.getPaymentStatus()).isEqualTo("PENDING");

        Booking paid = bookingService.confirmPayment(booking.getId());
        assertThat(paid.getPaymentStatus()).isEqualTo("PAID");
    }
}
