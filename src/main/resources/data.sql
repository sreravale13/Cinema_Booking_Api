-- ============================================================
--  Cinema Ticket Booking System — Sample Data
-- ============================================================
-- ── Movies ────────────────────────────────────────────────────
INSERT INTO movies (title, genre, director, duration_minutes, rating, language, release_date, description) VALUES
('Inception',           'Sci-Fi',   'Christopher Nolan',   148, 'PG-13', 'English', '2010-07-16', 'A thief who steals corporate secrets through dream-sharing technology.'),
('The Dark Knight',     'Action',   'Christopher Nolan',   152, 'PG-13', 'English', '2008-07-18', 'Batman faces the Joker, a criminal mastermind who plunges Gotham into anarchy.'),
('Interstellar',        'Sci-Fi',   'Christopher Nolan',   169, 'PG-13', 'English', '2014-11-07', 'Explorers travel through a wormhole in search of a new home for humanity.'),
('The Godfather',       'Drama',    'Francis Ford Coppola', 175, 'R',    'English', '1972-03-24', 'The aging patriarch of an organized crime dynasty transfers control to his reluctant son.'),
('Pulp Fiction',        'Crime',    'Quentin Tarantino',   154, 'R',    'English', '1994-10-14', 'The lives of two mob hitmen, a boxer, and others intertwine in Los Angeles.'),
('Avatar',              'Sci-Fi',   'James Cameron',       162, 'PG-13', 'English', '2009-12-18', 'A paraplegic marine on Pandora falls in love with a native alien and joins her people.'),
('The Lion King',       'Animation','Roger Allers',         88, 'G',    'English', '1994-07-15', 'A young lion prince flees his kingdom only to learn the true meaning of responsibility.'),
('Avengers: Endgame',  'Action',   'Russo Brothers',       181, 'PG-13', 'English', '2019-04-26', 'The Avengers assemble once more to reverse the actions of Thanos.'),
('Parasite',            'Thriller', 'Bong Joon-ho',        132, 'R',    'Korean',  '2019-11-08', 'Greed and class discrimination threaten a poor family when they scheme into a wealthy household.'),
('Dune',                'Sci-Fi',   'Denis Villeneuve',    155, 'PG-13', 'English', '2021-10-22', 'A noble family becomes embroiled in a war over the galaxy most valuable asset.');

-- ── Theaters ──────────────────────────────────────────────────
INSERT INTO theaters (name, total_seats, rows, seats_per_row, screen_type) VALUES
('Hall 1 - IMAX',    80, 8, 10, 'IMAX'),
('Hall 2 - Standard',60, 6, 10, 'STANDARD'),
('Hall 3 - 4DX',     40, 4, 10, '4DX');

-- ── Seats for Hall 1 (8 rows × 10 seats) ─────────────────────
INSERT INTO seats (theater_id, seat_number, row_label, seat_position, seat_type)
SELECT 1,
       CONCAT(chr, CAST(pos AS VARCHAR)),
       chr,
       pos,
       CASE WHEN chr IN ('A','B')         THEN 'VIP'
            WHEN chr IN ('C','D','E')     THEN 'PREMIUM'
            ELSE                               'STANDARD'
       END
FROM (VALUES ('A'),('B'),('C'),('D'),('E'),('F'),('G'),('H')) AS r(chr)
CROSS JOIN (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10)) AS c(pos);

-- ── Seats for Hall 2 (6 rows × 10 seats) ─────────────────────
INSERT INTO seats (theater_id, seat_number, row_label, seat_position, seat_type)
SELECT 2,
       CONCAT(chr, CAST(pos AS VARCHAR)),
       chr,
       pos,
       CASE WHEN chr IN ('A')       THEN 'VIP'
            WHEN chr IN ('B','C')   THEN 'PREMIUM'
            ELSE                         'STANDARD'
       END
FROM (VALUES ('A'),('B'),('C'),('D'),('E'),('F')) AS r(chr)
CROSS JOIN (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10)) AS c(pos);

-- ── Seats for Hall 3 (4 rows × 10 seats) ─────────────────────
INSERT INTO seats (theater_id, seat_number, row_label, seat_position, seat_type)
SELECT 3,
       CONCAT(chr, CAST(pos AS VARCHAR)),
       chr,
       pos,
       'STANDARD'
FROM (VALUES ('A'),('B'),('C'),('D')) AS r(chr)
CROSS JOIN (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10)) AS c(pos);

-- ── Showtimes ─────────────────────────────────────────────────
INSERT INTO showtimes (movie_id, theater_id, start_time, end_time, ticket_price, available_seats, status) VALUES
(1, 1, TIMESTAMPADD(DAY, 1, PARSEDATETIME('2026-04-19 10:00', 'yyyy-MM-dd HH:mm')),
       TIMESTAMPADD(DAY, 1, PARSEDATETIME('2026-04-19 12:28', 'yyyy-MM-dd HH:mm')), 18.50, 80, 'SCHEDULED'),
(2, 2, TIMESTAMPADD(DAY, 1, PARSEDATETIME('2026-04-19 13:00', 'yyyy-MM-dd HH:mm')),
       TIMESTAMPADD(DAY, 1, PARSEDATETIME('2026-04-19 15:32', 'yyyy-MM-dd HH:mm')), 14.00, 60, 'SCHEDULED'),
(3, 1, TIMESTAMPADD(DAY, 1, PARSEDATETIME('2026-04-19 15:00', 'yyyy-MM-dd HH:mm')),
       TIMESTAMPADD(DAY, 1, PARSEDATETIME('2026-04-19 17:49', 'yyyy-MM-dd HH:mm')), 18.50, 80, 'SCHEDULED'),
(6, 3, TIMESTAMPADD(DAY, 1, PARSEDATETIME('2026-04-19 18:00', 'yyyy-MM-dd HH:mm')),
       TIMESTAMPADD(DAY, 1, PARSEDATETIME('2026-04-19 20:42', 'yyyy-MM-dd HH:mm')), 22.00, 40, 'SCHEDULED'),
(8, 1, TIMESTAMPADD(DAY, 2, PARSEDATETIME('2026-04-20 19:00', 'yyyy-MM-dd HH:mm')),
       TIMESTAMPADD(DAY, 2, PARSEDATETIME('2026-04-20 22:01', 'yyyy-MM-dd HH:mm')), 18.50, 80, 'SCHEDULED'),
(9, 2, TIMESTAMPADD(DAY, 2, PARSEDATETIME('2026-04-20 20:30', 'yyyy-MM-dd HH:mm')),
       TIMESTAMPADD(DAY, 2, PARSEDATETIME('2026-04-20 22:42', 'yyyy-MM-dd HH:mm')), 14.00, 60, 'SCHEDULED'),
(10,1, TIMESTAMPADD(DAY, 3, PARSEDATETIME('2026-04-21 14:00', 'yyyy-MM-dd HH:mm')),
       TIMESTAMPADD(DAY, 3, PARSEDATETIME('2026-04-21 16:35', 'yyyy-MM-dd HH:mm')), 18.50, 80, 'SCHEDULED');

-- ── Customers ─────────────────────────────────────────────────
INSERT INTO customers (first_name, last_name, email, phone) VALUES
('Alice',   'Johnson',  'alice.johnson@email.com',  '555-0101'),
('Bob',     'Smith',    'bob.smith@email.com',      '555-0102'),
('Carol',   'Williams', 'carol.williams@email.com', '555-0103'),
('David',   'Brown',    'david.brown@email.com',    '555-0104'),
('Eva',     'Martinez', 'eva.martinez@email.com',   '555-0105');
