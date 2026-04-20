# 🎬 Cinema Ticket Booking System — Spring Boot + JDBC

A fully-featured REST API backend for managing movies, theaters, showtimes, seats, customers, and ticket bookings.

---

## 🏗️ Tech Stack

| Layer        | Technology                     |
|--------------|-------------------------------|
| Framework    | Spring Boot 3.2                |
| Data Access  | Spring JDBC (`JdbcTemplate`)   |
| Database     | H2 (in-memory, swap for MySQL) |
| Validation   | Jakarta Bean Validation        |
| Build        | Maven                          |
| Java         | 17+                            |

---

## 🚀 Getting Started

### Run the application
```bash
mvn spring-boot:run
```

### Access the H2 Console
```
URL:      http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:cinemadb
User:     sa
Password: (empty)
```

### Run tests
```bash
mvn test
```

---

## 🗄️ Database Schema

```
movies          — Film catalogue
theaters        — Screening rooms (STANDARD / IMAX / 4DX)
seats           — Physical seats per theater (STANDARD / PREMIUM / VIP)
showtimes       — Movie screenings with price & availability
customers       — Registered users
bookings        — Confirmed ticket purchases
booking_seats   — Which seats belong to which booking
```

---

## 📡 REST API Reference

### Movies  `/api/movies`
| Method | Endpoint              | Description                    |
|--------|-----------------------|--------------------------------|
| GET    | `/api/movies`         | List all movies                |
| GET    | `/api/movies?genre=`  | Filter by genre                |
| GET    | `/api/movies?search=` | Search by title keyword        |
| GET    | `/api/movies/{id}`    | Get movie by ID                |
| POST   | `/api/movies`         | Add a new movie                |
| PUT    | `/api/movies/{id}`    | Update a movie                 |
| DELETE | `/api/movies/{id}`    | Delete a movie                 |

### Theaters  `/api/theaters`
| Method | Endpoint              | Description          |
|--------|-----------------------|----------------------|
| GET    | `/api/theaters`       | List all theaters    |
| GET    | `/api/theaters/{id}`  | Get theater by ID    |
| POST   | `/api/theaters`       | Add a theater        |
| PUT    | `/api/theaters/{id}`  | Update a theater     |
| DELETE | `/api/theaters/{id}`  | Delete a theater     |

### Showtimes  `/api/showtimes`
| Method | Endpoint                          | Description                    |
|--------|-----------------------------------|--------------------------------|
| GET    | `/api/showtimes`                  | List all showtimes             |
| GET    | `/api/showtimes?movieId=`         | By movie                       |
| GET    | `/api/showtimes?availableOnly=true` | Only seats-available shows   |
| GET    | `/api/showtimes?from=&to=`        | By date range (ISO 8601)       |
| GET    | `/api/showtimes/{id}`             | Get by ID                      |
| POST   | `/api/showtimes`                  | Schedule a showtime            |
| PUT    | `/api/showtimes/{id}`             | Update a showtime              |
| PATCH  | `/api/showtimes/{id}/cancel`      | Cancel a showtime              |
| DELETE | `/api/showtimes/{id}`             | Delete a showtime              |

### Seats  `/api/seats`
| Method | Endpoint                                  | Description                |
|--------|-------------------------------------------|----------------------------|
| GET    | `/api/seats/theater/{theaterId}`          | All seats in a theater     |
| GET    | `/api/seats/showtime/{showtimeId}/available` | Available seats         |
| GET    | `/api/seats/showtime/{showtimeId}/booked`    | Booked seats            |

### Customers  `/api/customers`
| Method | Endpoint                         | Description              |
|--------|----------------------------------|--------------------------|
| GET    | `/api/customers`                 | List all customers       |
| GET    | `/api/customers/{id}`            | Get by ID                |
| GET    | `/api/customers/by-email?email=` | Find by email            |
| POST   | `/api/customers/register`        | Register new customer    |
| PUT    | `/api/customers/{id}`            | Update customer          |
| DELETE | `/api/customers/{id}`            | Delete customer          |

### Bookings  `/api/bookings`
| Method | Endpoint                           | Description              |
|--------|------------------------------------|--------------------------|
| GET    | `/api/bookings`                    | List all bookings        |
| GET    | `/api/bookings/{id}`               | Get by ID                |
| GET    | `/api/bookings/reference/{ref}`    | Get by booking reference |
| GET    | `/api/bookings/customer/{id}`      | By customer              |
| GET    | `/api/bookings/showtime/{id}`      | By showtime              |
| POST   | `/api/bookings`                    | Create a booking         |
| PATCH  | `/api/bookings/{id}/cancel`        | Cancel booking (refund)  |
| PATCH  | `/api/bookings/{id}/pay`           | Confirm payment          |

---

## 📋 Sample API Requests

### Create a Booking
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "showtimeId": 1,
    "seatIds": [1, 2, 3]
  }'
```

### Sample Response
```json
{
  "id": 1,
  "bookingReference": "BKG-20260419-A3F9B2",
  "customerId": 1,
  "customerName": "Alice Johnson",
  "movieTitle": "Inception",
  "showtime": "2026-04-20 10:00:00",
  "theaterName": "Hall 1 - IMAX",
  "numberOfTickets": 3,
  "totalAmount": 55.50,
  "seatNumbers": ["A1", "A2", "A3"],
  "status": "CONFIRMED",
  "paymentStatus": "PENDING"
}
```

### Cancel a Booking
```bash
curl -X PATCH http://localhost:8080/api/bookings/1/cancel
```

### Confirm Payment
```bash
curl -X PATCH http://localhost:8080/api/bookings/1/pay
```

---

## 🔁 Booking State Machine

```
PENDING ──► CONFIRMED ──► CANCELLED
                │
                └──► PAID (via /pay endpoint)
```

---

## 🔌 Switching to MySQL / PostgreSQL

1. Replace the H2 dependency in `pom.xml` with your driver:
```xml
<!-- MySQL -->
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cinemadb
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

3. Remove H2-specific SQL syntax from `schema.sql` and `data.sql`
   (mainly `TIMESTAMPADD`, `PARSEDATETIME` → use `DATE_ADD`, `STR_TO_DATE` for MySQL).

---

## 📁 Project Structure

```
cinema-booking/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/cinema/
    │   │   ├── CinemaBookingApplication.java
    │   │   ├── controller/
    │   │   │   ├── BookingController.java
    │   │   │   ├── CustomerController.java
    │   │   │   ├── MovieController.java
    │   │   │   ├── SeatController.java
    │   │   │   ├── ShowtimeController.java
    │   │   │   └── TheaterController.java
    │   │   ├── exception/
    │   │   │   ├── BookingException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── ResourceNotFoundException.java
    │   │   ├── model/
    │   │   │   ├── Booking.java
    │   │   │   ├── BookingSeat.java
    │   │   │   ├── Customer.java
    │   │   │   ├── Movie.java
    │   │   │   ├── Seat.java
    │   │   │   ├── Showtime.java
    │   │   │   └── Theater.java
    │   │   ├── repository/
    │   │   │   ├── BookingRepository.java
    │   │   │   ├── CustomerRepository.java
    │   │   │   ├── MovieRepository.java
    │   │   │   ├── SeatRepository.java
    │   │   │   ├── ShowtimeRepository.java
    │   │   │   └── TheaterRepository.java
    │   │   └── service/
    │   │       ├── BookingService.java
    │   │       ├── CustomerService.java
    │   │       ├── MovieService.java
    │   │       ├── SeatService.java
    │   │       ├── ShowtimeService.java
    │   │       └── TheaterService.java
    │   └── resources/
    │       ├── application.properties
    │       ├── data.sql
    │       └── schema.sql
    └── test/
        └── java/com/cinema/
            └── CinemaBookingIntegrationTest.java
```
