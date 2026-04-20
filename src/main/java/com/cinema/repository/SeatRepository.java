package com.cinema.repository;

import com.cinema.model.Seat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class SeatRepository {

    private final JdbcTemplate jdbcTemplate;

    public SeatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Seat> seatRowMapper = (rs, rowNum) -> Seat.builder()
            .id(rs.getLong("id"))
            .theaterId(rs.getLong("theater_id"))
            .seatNumber(rs.getString("seat_number"))
            .rowLabel(rs.getString("row_label"))
            .seatPosition(rs.getInt("seat_position"))
            .seatType(rs.getString("seat_type"))
            .build();

    public List<Seat> findByTheaterId(Long theaterId) {
        String sql = "SELECT * FROM seats WHERE theater_id = ? ORDER BY row_label, seat_position";
        return jdbcTemplate.query(sql, seatRowMapper, theaterId);
    }

    public Optional<Seat> findById(Long id) {
        List<Seat> result = jdbcTemplate.query("SELECT * FROM seats WHERE id = ?", seatRowMapper, id);
        return result.stream().findFirst();
    }

    /**
     * Returns seats NOT yet booked for a given showtime.
     */
    public List<Seat> findAvailableSeats(Long showtimeId, Long theaterId) {
        String sql = """
                SELECT s.* FROM seats s
                WHERE s.theater_id = ?
                AND s.id NOT IN (
                    SELECT bs.seat_id FROM booking_seats bs
                    JOIN bookings b ON bs.booking_id = b.id
                    WHERE bs.showtime_id = ? AND b.status != 'CANCELLED'
                )
                ORDER BY s.row_label, s.seat_position
                """;
        return jdbcTemplate.query(sql, seatRowMapper, theaterId, showtimeId);
    }

    public List<Seat> findBookedSeats(Long showtimeId) {
        String sql = """
                SELECT s.* FROM seats s
                JOIN booking_seats bs ON s.id = bs.seat_id
                JOIN bookings b ON bs.booking_id = b.id
                WHERE bs.showtime_id = ? AND b.status != 'CANCELLED'
                ORDER BY s.row_label, s.seat_position
                """;
        return jdbcTemplate.query(sql, seatRowMapper, showtimeId);
    }

    public Seat save(Seat seat) {
        String sql = "INSERT INTO seats (theater_id, seat_number, row_label, seat_position, seat_type) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, seat.getTheaterId());
            ps.setString(2, seat.getSeatNumber());
            ps.setString(3, seat.getRowLabel());
            ps.setInt(4, seat.getSeatPosition());
            ps.setString(5, seat.getSeatType());
            return ps;
        }, keyHolder);
        seat.setId(keyHolder.getKey().longValue());
        return seat;
    }
}
