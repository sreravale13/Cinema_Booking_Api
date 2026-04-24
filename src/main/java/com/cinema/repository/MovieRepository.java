package com.cinema.repository;

import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Movie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MovieRepository {

    private final JdbcTemplate jdbcTemplate;

    public MovieRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Movie> movieRowMapper = (rs, rowNum) -> Movie.builder()
            .id(rs.getLong("id"))
            .title(rs.getString("title"))
            .genre(rs.getString("genre"))
            .director(rs.getString("director"))
            .durationMinutes(rs.getInt("duration_minutes"))
            .rating(rs.getString("rating"))
            .language(rs.getString("language"))
//            .releaseDate(rs.getDate("release_date") != null
//                    ? rs.getDate("release_date").toString() : null)
            .description(rs.getString("description"))
            .build();

    public List<Movie> findAll() {
        String sql = "SELECT * FROM movies";
        List<Movie> mv= jdbcTemplate.query(sql, movieRowMapper);
        return mv;
    }

    public Optional<Movie> findById(Long id) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        List<Movie> movies = jdbcTemplate.query(sql, movieRowMapper, id);
        return movies.stream().findFirst();
    }

    public List<Movie> findByGenre(String genre) {
        String sql = "SELECT * FROM movies WHERE LOWER(genre) = LOWER(?) ORDER BY title";
        return jdbcTemplate.query(sql, movieRowMapper, genre);
    }

    public List<Movie> searchByTitle(String keyword) {
        String sql = "SELECT * FROM movies WHERE LOWER(title) LIKE LOWER(?) ORDER BY title";
        return jdbcTemplate.query(sql, movieRowMapper, "%" + keyword + "%");
    }

    public Movie save(Movie movie) {
        String sql = """
                INSERT INTO movies (title, genre, director, duration_minutes, rating, language, release_date, description)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getGenre());
            ps.setString(3, movie.getDirector());
            ps.setInt(4, movie.getDurationMinutes());
            ps.setString(5, movie.getRating());
            ps.setString(6, movie.getLanguage());
            ps.setObject(7, movie.getReleaseDate());
            ps.setString(8, movie.getDescription());
            return ps;
        }, keyHolder);
        movie.setId(keyHolder.getKey().longValue());
        return movie;
    }

    public Movie update(Movie movie) {
        String sql = """
                UPDATE movies SET title=?, genre=?, director=?, duration_minutes=?,
                rating=?, language=?, release_date=?, description=? WHERE id=?
                """;
        int rows = jdbcTemplate.update(sql,
                movie.getTitle(), movie.getGenre(), movie.getDirector(),
                movie.getDurationMinutes(), movie.getRating(), movie.getLanguage(),
                movie.getReleaseDate(), movie.getDescription(), movie.getId());
        if (rows == 0) throw new ResourceNotFoundException("Movie not found with id: " + movie.getId());
        return movie;
    }

    public void deleteById(Long id) {
        int rows = jdbcTemplate.update("DELETE FROM movies WHERE id = ?", id);
        if (rows == 0) throw new ResourceNotFoundException("Movie not found with id: " + id);
    }
}
