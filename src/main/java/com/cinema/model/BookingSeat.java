package com.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeat {
    private Long id;
    private Long bookingId;
    private Long seatId;
    private Long showtimeId;
    private String seatNumber;
}
