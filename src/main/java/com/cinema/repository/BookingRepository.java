package com.cinema.repository;

import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Booking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Booking> bookingRowMapper = (rs, rowNum) -> Booking.builder()
            .id(rs.getLong("id"))
            .customerId(rs.getLong("customer_id"))
            .showtimeId(rs.getLong("showtime_id"))
            .bookingReference(rs.getString("booking_reference"))
            .numberOfTickets(rs.getInt("number_of_tickets"))
            .totalAmount(rs.getBigDecimal("total_amount"))
            .status(rs.getString("status"))
            .paymentStatus(rs.getString("payment_status"))
            .bookedAt(rs.getTimestamp("booked_at").toLocalDateTime())
            .customerName(rs.getString("customer_name"))
            .movieTitle(rs.getString("movie_title"))
            .showtime(rs.getTimestamp("show_start") != null
                    ? rs.getTimestamp("show_start").toLocalDateTime() : null)
            .theaterName(rs.getString("theater_name"))
            .build();

    private static final String SELECT_WITH_JOINS = """
            SELECT b.*,
                   CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
                   m.title AS movie_title,
                   s.start_time AS show_start,
                   t.name AS theater_name
            FROM bookings b
            JOIN customers c ON b.customer_id = c.id
            JOIN showtimes s ON b.showtime_id = s.id
            JOIN movies m ON s.movie_id = m.id
            JOIN theaters t ON s.theater_id = t.id
            """;

    public List<Booking> findAll() {
        return jdbcTemplate.query(SELECT_WITH_JOINS + " ORDER BY b.booked_at DESC", bookingRowMapper);
    }

    public Optional<Booking> findById(Long id) {
        List<Booking> result = jdbcTemplate.query(SELECT_WITH_JOINS + " WHERE b.id = ?", bookingRowMapper, id);
        return result.stream().findFirst();
    }

    public Optional<Booking> findByReference(String reference) {
        List<Booking> result = jdbcTemplate.query(
                SELECT_WITH_JOINS + " WHERE b.booking_reference = ?", bookingRowMapper, reference);
        return result.stream().findFirst();
    }

    public List<Booking> findByCustomerId(Long customerId) {
        return jdbcTemplate.query(
                SELECT_WITH_JOINS + " WHERE b.customer_id = ? ORDER BY b.booked_at DESC",
                bookingRowMapper, customerId);
    }

    public List<Booking> findByShowtimeId(Long showtimeId) {
        return jdbcTemplate.query(
                SELECT_WITH_JOINS + " WHERE b.showtime_id = ? ORDER BY b.booked_at DESC",
                bookingRowMapper, showtimeId);
    }

    public Booking save(Booking booking) {
        String sql = """
                INSERT INTO bookings (customer_id, showtime_id, booking_reference, number_of_tickets,
                total_amount, status, payment_status, booked_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, booking.getCustomerId());
            ps.setLong(2, booking.getShowtimeId());
            ps.setString(3, booking.getBookingReference());
            ps.setInt(4, booking.getNumberOfTickets());
            ps.setBigDecimal(5, booking.getTotalAmount());
            ps.setString(6, booking.getStatus() != null ? booking.getStatus() : "CONFIRMED");
            ps.setString(7, booking.getPaymentStatus() != null ? booking.getPaymentStatus() : "PENDING");
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        booking.setId(keyHolder.getKey().longValue());
        return booking;
    }

    public void saveBookingSeat(Long bookingId, Long seatId, Long showtimeId) {
        jdbcTemplate.update(
                "INSERT INTO booking_seats (booking_id, seat_id, showtime_id) VALUES (?, ?, ?)",
                bookingId, seatId, showtimeId);
    }

    public List<String> findSeatNumbersByBookingId(Long bookingId) {
        String sql = """
                SELECT s.seat_number FROM booking_seats bs
                JOIN seats s ON bs.seat_id = s.id
                WHERE bs.booking_id = ?
                ORDER BY s.row_label, s.seat_position
                """;
        return jdbcTemplate.queryForList(sql, String.class, bookingId);
    }

    public void updateStatus(Long id, String status) {
        int rows = jdbcTemplate.update("UPDATE bookings SET status = ? WHERE id = ?", status, id);
        if (rows == 0) throw new ResourceNotFoundException("Booking not found with id: " + id);
    }

    public void updatePaymentStatus(Long id, String paymentStatus) {
        int rows = jdbcTemplate.update("UPDATE bookings SET payment_status = ? WHERE id = ?", paymentStatus, id);
        if (rows == 0) throw new ResourceNotFoundException("Booking not found with id: " + id);
    }

    public long countBookingsByShowtime(Long showtimeId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bookings WHERE showtime_id = ? AND status != 'CANCELLED'",
                Long.class, showtimeId);
        return count != null ? count : 0L;
    }
}
