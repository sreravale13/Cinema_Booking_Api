package com.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private Long id;
    private String title;
    private String genre;
    private String director;
    private int durationMinutes;
    private String rating;         // G, PG, PG-13, R
    private String language;
    private LocalDate releaseDate;
    private String description;
}
