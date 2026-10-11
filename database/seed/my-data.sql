-- =====================================================================
-- TransitHub: MY data (replaces the demo data in sample-data.sql)
--
-- Docker runs this file ONCE, right after schema.sql, when the database
-- is brand new. To run it again: docker compose down -v, then up -d.
--
-- Add your own rows below, in this order (a row can only point to rows
-- that already exist): stops -> transportations -> drivers -> vehicles
-- -> routes -> route_stops -> route_points -> fares -> schedules -> alerts
-- =====================================================================

-- ---------------------------------------------------------------------
-- STOPS
-- latitude first, then longitude. A name with "Terminal" in it is drawn
-- as a green terminal marker on the map.
-- Batangas Grand Terminal: verified location on Diversion Road, Alangilan.
-- ---------------------------------------------------------------------
INSERT INTO stops (id, name, description, latitude, longitude, is_demo_data) VALUES
(1, 'Batangas Grand Terminal', 'Diversion Road, Alangilan, Batangas City', 13.790168, 121.061538, FALSE);

-- (your next stops go here: id 2, 3, ...)


-- ---------------------------------------------------------------------
-- Keep these lines LAST. Because the rows above set their id by hand,
-- each id counter must jump past the highest id, or the next row added
-- in the admin screens fails with "duplicate key".
-- (A table that is still empty is simply skipped.)
-- ---------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('stops', 'id'),            (SELECT MAX(id) FROM stops));
SELECT setval(pg_get_serial_sequence('transportations', 'id'),  (SELECT MAX(id) FROM transportations));
SELECT setval(pg_get_serial_sequence('drivers', 'id'),          (SELECT MAX(id) FROM drivers));
SELECT setval(pg_get_serial_sequence('vehicles', 'id'),         (SELECT MAX(id) FROM vehicles));
SELECT setval(pg_get_serial_sequence('routes', 'id'),           (SELECT MAX(id) FROM routes));
