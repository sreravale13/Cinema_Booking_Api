package com.cinema.service;

import com.cinema.exception.BookingException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Booking;
import com.cinema.model.Seat;
import com.cinema.model.Showtime;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.SeatRepository;
import com.cinema.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;

    public BookingService(BookingRepository bookingRepository,
                          ShowtimeRepository showtimeRepository,
                          SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        bookings.forEach(this::enrichWithSeats);
        return bookings;
    }

    public Booking getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        enrichWithSeats(booking);
        return booking;
    }

    public Booking getBookingByReference(String reference) {
        Booking booking = bookingRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + reference));
        enrichWithSeats(booking);
        return booking;
    }

    public List<Booking> getBookingsByCustomer(Long customerId) {
        List<Booking> bookings = bookingRepository.findByCustomerId(customerId);
        bookings.forEach(this::enrichWithSeats);
        return bookings;
    }

    public List<Booking> getBookingsByShowtime(Long showtimeId) {
        List<Booking> bookings = bookingRepository.findByShowtimeId(showtimeId);
        bookings.forEach(this::enrichWithSeats);
        return bookings;
    }

    @Transactional
    public Booking createBooking(Long customerId, Long showtimeId, List<Long> seatIds) {
        // 1. Validate showtime
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + showtimeId));

        if (!"SCHEDULED".equals(showtime.getStatus())) {
            throw new BookingException("Cannot book tickets for a showtime with status: " + showtime.getStatus());
        }
        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BookingException("Cannot book tickets for a past showtime.");
        }
        if (showtime.getAvailableSeats() < seatIds.size()) {
            throw new BookingException("Not enough available seats. Requested: " + seatIds.size()
                    + ", Available: " + showtime.getAvailableSeats());
        }

        // 2. Validate each seat is available
        List<Seat> availableSeats = seatRepository.findAvailableSeats(showtimeId, showtime.getTheaterId());
        List<Long> availableSeatIds = availableSeats.stream().map(Seat::getId).toList();
        for (Long seatId : seatIds) {
            if (!availableSeatIds.contains(seatId)) {
                throw new BookingException("Seat ID " + seatId + " is not available for this showtime.");
            }
        }

        // 3. Calculate total price
        BigDecimal totalAmount = showtime.getTicketPrice()
                .multiply(BigDecimal.valueOf(seatIds.size()));

        // 4. Create the booking
        Booking booking = Booking.builder()
                .customerId(customerId)
                .showtimeId(showtimeId)
                .bookingReference(generateReference())
                .numberOfTickets(seatIds.size())
                .totalAmount(totalAmount)
                .status("CONFIRMED")
                .paymentStatus("PENDING")
                .build();

        Booking saved = bookingRepository.save(booking);

        // 5. Assign seats
        for (Long seatId : seatIds) {
            bookingRepository.saveBookingSeat(saved.getId(), seatId, showtimeId);
        }

        // 6. Update available seat count
        showtimeRepository.updateAvailableSeats(showtimeId, -seatIds.size());

        // 7. Enrich and return
        enrichWithSeats(saved);
        return saved;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BookingException("Booking is already cancelled.");
        }

        // Update booking status
        bookingRepository.updateStatus(bookingId, "CANCELLED");
        bookingRepository.updatePaymentStatus(bookingId, "REFUNDED");

        // Restore available seats
        showtimeRepository.updateAvailableSeats(booking.getShowtimeId(), booking.getNumberOfTickets());

        booking.setStatus("CANCELLED");
        booking.setPaymentStatus("REFUNDED");
        enrichWithSeats(booking);
        return booking;
    }

    @Transactional
    public Booking confirmPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new BookingException("Only CONFIRMED bookings can receive payment.");
        }
        bookingRepository.updatePaymentStatus(bookingId, "PAID");
        booking.setPaymentStatus("PAID");
        enrichWithSeats(booking);
        return booking;
    }

    private void enrichWithSeats(Booking booking) {
        List<String> seatNumbers = bookingRepository.findSeatNumbersByBookingId(booking.getId());
        booking.setSeatNumbers(seatNumbers);
    }

    private String generateReference() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "BKG-" + datePart + "-" + uniquePart;
    }
}
