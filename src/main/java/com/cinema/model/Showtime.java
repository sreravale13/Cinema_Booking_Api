package com.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    private Long id;
    private Long movieId;
    private Long theaterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal ticketPrice;
    private int availableSeats;
    private String status;          // SCHEDULED, ONGOING, COMPLETED, CANCELLED

    // Joined fields
    private String movieTitle;
    private String theaterName;
}
