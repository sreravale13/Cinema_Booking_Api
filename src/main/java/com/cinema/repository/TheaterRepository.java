package com.cinema.repository;

import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Theater;
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
public class TheaterRepository {

    private final JdbcTemplate jdbcTemplate;

    public TheaterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Theater> theaterRowMapper = (rs, rowNum) -> Theater.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .totalSeats(rs.getInt("total_seats"))
            .rows(rs.getInt("rows"))
            .seatsPerRow(rs.getInt("seats_per_row"))
            .screenType(rs.getString("screen_type"))
            .build();

    public List<Theater> findAll() {
        return jdbcTemplate.query("SELECT * FROM theaters ORDER BY name", theaterRowMapper);
    }

    public Optional<Theater> findById(Long id) {
        List<Theater> result = jdbcTemplate.query("SELECT * FROM theaters WHERE id = ?", theaterRowMapper, id);
        return result.stream().findFirst();
    }

    public Theater save(Theater theater) {
        String sql = "INSERT INTO theaters (name, total_seats, rows, seats_per_row, screen_type) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, theater.getName());
            ps.setInt(2, theater.getTotalSeats());
            ps.setInt(3, theater.getRows());
            ps.setInt(4, theater.getSeatsPerRow());
            ps.setString(5, theater.getScreenType());
            return ps;
        }, keyHolder);
        theater.setId(keyHolder.getKey().longValue());
        return theater;
    }

    public Theater update(Theater theater) {
        String sql = "UPDATE theaters SET name=?, total_seats=?, rows=?, seats_per_row=?, screen_type=? WHERE id=?";
        int rows = jdbcTemplate.update(sql,
                theater.getName(), theater.getTotalSeats(), theater.getRows(),
                theater.getSeatsPerRow(), theater.getScreenType(), theater.getId());
        if (rows == 0) throw new ResourceNotFoundException("Theater not found with id: " + theater.getId());
        return theater;
    }

    public void deleteById(Long id) {
        int rows = jdbcTemplate.update("DELETE FROM theaters WHERE id = ?", id);
        if (rows == 0) throw new ResourceNotFoundException("Theater not found with id: " + id);
    }
}
