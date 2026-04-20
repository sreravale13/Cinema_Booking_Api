package com.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theater {
    private Long id;
    private String name;
    private int totalSeats;
    private int rows;
    private int seatsPerRow;
    private String screenType;     // STANDARD, IMAX, 4DX
}
