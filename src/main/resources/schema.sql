-- ============================================================
--  Cinema Ticket Booking System — DDL (H2 compatible)
-- ============================================================

DROP TABLE IF EXISTS booking_seats;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS showtimes;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS theaters;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS customers;

-- ── Movies ────────────────────────────────────────────────────
CREATE TABLE movies (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(200)  NOT NULL,
    genre            VARCHAR(50)   NOT NULL,
    director         VARCHAR(100),
    duration_minutes INT           NOT NULL,
    rating           VARCHAR(10),
    language         VARCHAR(50)   DEFAULT 'English',
    release_date     DATE,
    description      TEXT
);

-- ── Theaters ──────────────────────────────────────────────────
CREATE TABLE theaters (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    total_seats   INT          NOT NULL,
    rows          INT          NOT NULL,
    seats_per_row INT          NOT NULL,
    screen_type   VARCHAR(20)  DEFAULT 'STANDARD'  -- STANDARD | IMAX | 4DX
);

-- ── Seats (one row per physical seat) ─────────────────────────
CREATE TABLE seats (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    theater_id    BIGINT       NOT NULL REFERENCES theaters(id) ON DELETE CASCADE,
    seat_number   VARCHAR(10)  NOT NULL,   -- e.g. A1, B12
    row_label     VARCHAR(5)   NOT NULL,
    seat_position INT          NOT NULL,
    seat_type     VARCHAR(20)  DEFAULT 'STANDARD',  -- STANDARD | PREMIUM | VIP
    UNIQUE (theater_id, seat_number)
);

-- ── Showtimes ─────────────────────────────────────────────────
CREATE TABLE showtimes (
    id              BIGINT         AUTO_INCREMENT PRIMARY KEY,
    movie_id        BIGINT         NOT NULL REFERENCES movies(id)   ON DELETE CASCADE,
    theater_id      BIGINT         NOT NULL REFERENCES theaters(id) ON DELETE CASCADE,
    start_time      TIMESTAMP      NOT NULL,
    end_time        TIMESTAMP      NOT NULL,
    ticket_price    DECIMAL(10,2)  NOT NULL,
    available_seats INT            NOT NULL,
    status          VARCHAR(20)    DEFAULT 'SCHEDULED'  -- SCHEDULED | ONGOING | COMPLETED | CANCELLED
);

-- ── Customers ─────────────────────────────────────────────────
CREATE TABLE customers (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(200) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    registered_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ── Bookings ──────────────────────────────────────────────────
CREATE TABLE bookings (
    id                BIGINT         AUTO_INCREMENT PRIMARY KEY,
    customer_id       BIGINT         NOT NULL REFERENCES customers(id),
    showtime_id       BIGINT         NOT NULL REFERENCES showtimes(id),
    booking_reference VARCHAR(50)    NOT NULL UNIQUE,
    number_of_tickets INT            NOT NULL,
    total_amount      DECIMAL(10,2)  NOT NULL,
    status            VARCHAR(20)    DEFAULT 'CONFIRMED',  -- CONFIRMED | CANCELLED | PENDING
    payment_status    VARCHAR(20)    DEFAULT 'PENDING',    -- PAID | PENDING | REFUNDED
    booked_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- ── Booking → Seats (junction) ────────────────────────────────
CREATE TABLE booking_seats (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id  BIGINT NOT NULL REFERENCES bookings(id)  ON DELETE CASCADE,
    seat_id     BIGINT NOT NULL REFERENCES seats(id),
    showtime_id BIGINT NOT NULL REFERENCES showtimes(id),
    UNIQUE (seat_id, showtime_id)   -- prevents double-booking the same seat
);

-- ── Indexes for performance ───────────────────────────────────
CREATE INDEX idx_showtimes_movie    ON showtimes(movie_id);
CREATE INDEX idx_showtimes_theater  ON showtimes(theater_id);
CREATE INDEX idx_showtimes_start    ON showtimes(start_time);
CREATE INDEX idx_bookings_customer  ON bookings(customer_id);
CREATE INDEX idx_bookings_showtime  ON bookings(showtime_id);
CREATE INDEX idx_booking_seats_bk   ON booking_seats(booking_id);
CREATE INDEX idx_booking_seats_sw   ON booking_seats(showtime_id);
