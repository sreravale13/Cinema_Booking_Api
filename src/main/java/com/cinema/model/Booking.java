package com.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private Long customerId;
    private Long showtimeId;
    private String bookingReference;
    private int numberOfTickets;
    private BigDecimal totalAmount;
    private String status;          // CONFIRMED, CANCELLED, PENDING
    private String paymentStatus;   // PAID, PENDING, REFUNDED
    private LocalDateTime bookedAt;

    // Joined fields
    private String customerName;
    private String movieTitle;
    private LocalDateTime showtime;
    private String theaterName;
    private List<String> seatNumbers;
}
