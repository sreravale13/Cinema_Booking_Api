package com.cinema.service;

import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Theater;
import com.cinema.repository.TheaterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheaterService {

    private final TheaterRepository theaterRepository;

    public TheaterService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    public Theater getTheaterById(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + id));
    }

    public Theater createTheater(Theater theater) {
        theater.setTotalSeats(theater.getRows() * theater.getSeatsPerRow());
        return theaterRepository.save(theater);
    }

    public Theater updateTheater(Long id, Theater theater) {
        getTheaterById(id); // ensures exists
        theater.setId(id);
        theater.setTotalSeats(theater.getRows() * theater.getSeatsPerRow());
        return theaterRepository.update(theater);
    }

    public void deleteTheater(Long id) {
        theaterRepository.deleteById(id);
    }
}
