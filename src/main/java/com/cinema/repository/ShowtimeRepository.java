package com.cinema.repository;

import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Showtime;
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
public class ShowtimeRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShowtimeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Showtime> showtimeRowMapper = (rs, rowNum) -> Showtime.builder()
            .id(rs.getLong("id"))
            .movieId(rs.getLong("movie_id"))
            .theaterId(rs.getLong("theater_id"))
            .startTime(rs.getTimestamp("start_time").toLocalDateTime())
            .endTime(rs.getTimestamp("end_time").toLocalDateTime())
            .ticketPrice(rs.getBigDecimal("ticket_price"))
            .availableSeats(rs.getInt("available_seats"))
            .status(rs.getString("status"))
            .movieTitle(rs.getString("movie_title"))
            .theaterName(rs.getString("theater_name"))
            .build();

    private static final String SELECT_WITH_JOINS = """
            SELECT s.*, m.title AS movie_title, t.name AS theater_name
            FROM showtimes s
            JOIN movies m ON s.movie_id = m.id
            JOIN theaters t ON s.theater_id = t.id
            """;

    public List<Showtime> findAll() {
        return jdbcTemplate.query(SELECT_WITH_JOINS + " ORDER BY s.start_time", showtimeRowMapper);
    }

    public Optional<Showtime> findById(Long id) {
        List<Showtime> result = jdbcTemplate.query(
                SELECT_WITH_JOINS + " WHERE s.id = ?", showtimeRowMapper, id);
        return result.stream().findFirst();
    }

    public List<Showtime> findByMovieId(Long movieId) {
        String sql = SELECT_WITH_JOINS + " WHERE s.movie_id = ? AND s.start_time >= NOW() ORDER BY s.start_time";
        return jdbcTemplate.query(sql, showtimeRowMapper, movieId);
    }

    public List<Showtime> findByDate(LocalDateTime from, LocalDateTime to) {
        String sql = SELECT_WITH_JOINS + " WHERE s.start_time BETWEEN ? AND ? ORDER BY s.start_time";
        return jdbcTemplate.query(sql, showtimeRowMapper, Timestamp.valueOf(from), Timestamp.valueOf(to));
    }

    public List<Showtime> findAvailable() {
        String sql = SELECT_WITH_JOINS + " WHERE s.available_seats > 0 AND s.start_time >= NOW() AND s.status = 'SCHEDULED' ORDER BY s.start_time";
        return jdbcTemplate.query(sql, showtimeRowMapper);
    }

    public Showtime save(Showtime showtime) {
        String sql = """
                INSERT INTO showtimes (movie_id, theater_id, start_time, end_time, ticket_price, available_seats, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, showtime.getMovieId());
            ps.setLong(2, showtime.getTheaterId());
            ps.setTimestamp(3, Timestamp.valueOf(showtime.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(showtime.getEndTime()));
            ps.setBigDecimal(5, showtime.getTicketPrice());
            ps.setInt(6, showtime.getAvailableSeats());
            ps.setString(7, showtime.getStatus() != null ? showtime.getStatus() : "SCHEDULED");
            return ps;
        }, keyHolder);
        showtime.setId(keyHolder.getKey().longValue());
        return showtime;
    }

    public void updateAvailableSeats(Long showtimeId, int delta) {
        jdbcTemplate.update(
                "UPDATE showtimes SET available_seats = available_seats + ? WHERE id = ?",
                delta, showtimeId);
    }

    public Showtime update(Showtime showtime) {
        String sql = """
                UPDATE showtimes SET movie_id=?, theater_id=?, start_time=?, end_time=?,
                ticket_price=?, available_seats=?, status=? WHERE id=?
                """;
        int rows = jdbcTemplate.update(sql,
                showtime.getMovieId(), showtime.getTheaterId(),
                Timestamp.valueOf(showtime.getStartTime()),
                Timestamp.valueOf(showtime.getEndTime()),
                showtime.getTicketPrice(), showtime.getAvailableSeats(),
                showtime.getStatus(), showtime.getId());
        if (rows == 0) throw new ResourceNotFoundException("Showtime not found with id: " + showtime.getId());
        return showtime;
    }

    public void deleteById(Long id) {
        int rows = jdbcTemplate.update("DELETE FROM showtimes WHERE id = ?", id);
        if (rows == 0) throw new ResourceNotFoundException("Showtime not found with id: " + id);
    }
}
