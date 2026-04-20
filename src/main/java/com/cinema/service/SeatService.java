package com.cinema.service;

import com.cinema.model.Seat;
import com.cinema.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> getSeatsByTheater(Long theaterId) {
        return seatRepository.findByTheaterId(theaterId);
    }

    public List<Seat> getAvailableSeats(Long showtimeId, Long theaterId) {
        return seatRepository.findAvailableSeats(showtimeId, theaterId);
    }

    public List<Seat> getBookedSeats(Long showtimeId) {
        return seatRepository.findBookedSeats(showtimeId);
    }
}
