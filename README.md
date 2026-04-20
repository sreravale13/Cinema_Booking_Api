

A fully-featured REST API backend for managing movies, theaters, showtimes, seats, customers, and ticket bookings.

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

