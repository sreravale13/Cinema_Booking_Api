package com.cinema.service;

import com.cinema.exception.BookingException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Seat;
import com.cinema.model.Showtime;
import com.cinema.model.Theater;
import com.cinema.repository.ShowtimeRepository;
import com.cinema.repository.TheaterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final TheaterRepository theaterRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository,
                           TheaterRepository theaterRepository) {
        this.showtimeRepository = showtimeRepository;
        this.theaterRepository = theaterRepository;
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public Showtime getShowtimeById(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));
    }

    public List<Showtime> getShowtimesByMovie(Long movieId) {
        return showtimeRepository.findByMovieId(movieId);
    }

    public List<Showtime> getShowtimesByDate(LocalDateTime from, LocalDateTime to) {
        return showtimeRepository.findByDate(from, to);
    }

    public List<Showtime> getAvailableShowtimes() {
        return showtimeRepository.findAvailable();
    }

    public Showtime createShowtime(Showtime showtime) {
        // Validate theater exists and set available seats
        Theater theater = theaterRepository.findById(showtime.getTheaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + showtime.getTheaterId()));

        // Check for time conflicts in the same theater
        List<Showtime> allShowtimes = showtimeRepository.findAll();
        boolean conflict = allShowtimes.stream()
                .filter(s -> s.getTheaterId().equals(showtime.getTheaterId()))
                .filter(s -> !"CANCELLED".equals(s.getStatus()))
                .anyMatch(s ->
                        showtime.getStartTime().isBefore(s.getEndTime()) &&
                        showtime.getEndTime().isAfter(s.getStartTime())
                );
        if (conflict) {
            throw new BookingException("Theater already has a showtime scheduled during this period.");
        }

        showtime.setAvailableSeats(theater.getTotalSeats());
        showtime.setStatus("SCHEDULED");
        return showtimeRepository.save(showtime);
    }

    public Showtime updateShowtime(Long id, Showtime showtime) {
        getShowtimeById(id); // ensures exists
        showtime.setId(id);
        return showtimeRepository.update(showtime);
    }

    public void cancelShowtime(Long id) {
        Showtime showtime = getShowtimeById(id);
        showtime.setStatus("CANCELLED");
        showtimeRepository.update(showtime);
    }

    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }
}
