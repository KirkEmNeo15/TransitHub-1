-- =====================================================================
-- Migration 001: remove the SHUTTLE and TRAIN transportation types.
-- Run this ONCE on a database that was created before this change.
-- (A brand-new database made from schema.sql + sample-data.sql does not need it.)
--
-- What it does:
--   1. moves the demo route "Lipa - SM City" from the demo shuttle service to the
--      "Lipa Local Jeepney (Demo)" service (it becomes a jeepney route)
--   2. moves the vehicle of the demo shuttle service to the same jeepney service
--   3. deletes the demo shuttle service
--   4. stops with an error if any other shuttle/train row still exists
--   5. removes the shuttle/train columns and tightens the type check
-- Everything runs in one transaction: if something fails, nothing is changed.
-- =====================================================================
BEGIN;

-- 1) the shuttle route becomes a jeepney route (jeepney fare: 13.00 base, 0.85 per km)
UPDATE routes
SET route_code = 'LS-JEEP-01',
    route_name = 'Lipa - SM City (Jeepney)',
    transportation_id = (SELECT id FROM transportations WHERE code = 'JEEP-LL')
WHERE route_code = 'LS-SHUT-01';

UPDATE fares
SET base_fare = 13.00, per_km_rate = 0.85
WHERE route_id = (SELECT id FROM routes WHERE route_code = 'LS-JEEP-01');

-- 2) the demo shuttle's vehicle goes to the local jeepney service
UPDATE vehicles
SET transportation_id = (SELECT id FROM transportations WHERE code = 'JEEP-LL')
WHERE transportation_id = (SELECT id FROM transportations WHERE code = 'SHT-SM');

-- 3) delete the demo shuttle service
DELETE FROM transportations WHERE code = 'SHT-SM';

-- 4) safety check: nothing else may still use the removed types
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM transportations WHERE transport_type IN ('SHUTTLE', 'TRAIN')) THEN
        RAISE EXCEPTION 'Other SHUTTLE/TRAIN transportations still exist. Move or delete them first, then run this script again.';
    END IF;
END $$;

-- 5) schema: only BUS, JEEPNEY and VAN are allowed from now on
ALTER TABLE transportations DROP CONSTRAINT IF EXISTS transportations_transport_type_check;
ALTER TABLE transportations
    ADD CONSTRAINT transportations_transport_type_check CHECK (transport_type IN ('BUS', 'JEEPNEY', 'VAN'));
ALTER TABLE transportations DROP COLUMN IF EXISTS service_area;
ALTER TABLE transportations DROP COLUMN IF EXISTS number_of_cars;

COMMIT;
