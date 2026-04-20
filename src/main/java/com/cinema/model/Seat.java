package com.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private Long id;
    private Long theaterId;
    private String seatNumber;     // e.g., A1, B5
    private String rowLabel;
    private int seatPosition;
    private String seatType;       // STANDARD, PREMIUM, VIP
}
