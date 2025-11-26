-- ============================================================
-- MIGRATION V3 -> V4
-- Point de départ : schéma créé par eventbyte_bdd_v3.sql (UUID)
--
-- Objectifs :
--  - catalog.events.id          : UUID -> VARCHAR(255)
--  - catalog.tickets.id         : UUID -> VARCHAR(255)
--  - catalog.tickets.event_id   : UUID -> VARCHAR(255)
--  - social.likes.event_id      : UUID -> VARCHAR(255)
--  - booking.bookings.event_id  : UUID -> VARCHAR(255)
--  - booking.booking_lines.event_id / ticket_id : UUID -> VARCHAR(255)
--  - assouplir description / cover_url sur events
--  - index geo = idx_events_geo
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- 1. Drop des contraintes qui bloquent les changements de type
-- ------------------------------------------------------------

-- booking_lines -> bookings / tickets
ALTER TABLE IF EXISTS booking.booking_lines
    DROP CONSTRAINT IF EXISTS fk_booking_lines_booking,
    DROP CONSTRAINT IF EXISTS fk_booking_lines_ticket;

-- bookings.event_id -> events.id (nom auto probable)
ALTER TABLE IF EXISTS booking.bookings
    DROP CONSTRAINT IF EXISTS bookings_event_id_fkey;

-- likes.event_id -> events.id (nom auto probable)
ALTER TABLE IF EXISTS social.likes
    DROP CONSTRAINT IF EXISTS likes_event_id_fkey;

-- tickets.event_id -> events.id
-- on couvre à la fois l’ancien nom auto et un éventuel nom explicite
ALTER TABLE IF EXISTS catalog.tickets
    DROP CONSTRAINT IF EXISTS fk_tickets_event,
    DROP CONSTRAINT IF EXISTS tickets_event_id_fkey;

-- ------------------------------------------------------------
-- 2. catalog.events : UUID -> VARCHAR(255)
-- ------------------------------------------------------------

ALTER TABLE catalog.events
    ALTER COLUMN id TYPE VARCHAR(255) USING id::text;

-- description et cover_url redeviennent NULLables
ALTER TABLE catalog.events
    ALTER COLUMN description DROP NOT NULL,
    ALTER COLUMN cover_url   DROP NOT NULL;

-- Renommage de l'index geo pour matcher ton script d'origine :
-- on part de v3 (idx_events_venue_coords) -> v4 (idx_events_geo)
DO $$
BEGIN
    -- Si l'index "idx_events_venue_coords" existe ET que "idx_events_geo" n'existe pas,
    -- on le renomme. Sinon on ne fait rien (idempotent).
    IF EXISTS (
        SELECT 1
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relkind = 'i'
          AND c.relname = 'idx_events_venue_coords'
          AND n.nspname = 'catalog'
    )
    AND NOT EXISTS (
        SELECT 1
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relkind = 'i'
          AND c.relname = 'idx_events_geo'
          AND n.nspname = 'catalog'
    ) THEN
        ALTER INDEX catalog.idx_events_venue_coords
            RENAME TO idx_events_geo;
    END IF;
END$$;

-- Au cas où l'index n'existerait pas encore sous ce nom
CREATE INDEX IF NOT EXISTS idx_events_geo
    ON catalog.events (venue_latitude, venue_longitude);

-- Index de tri par date (déjà dans v3, on sécurise)
CREATE INDEX IF NOT EXISTS idx_events_start_at
    ON catalog.events (start_at);

-- ------------------------------------------------------------
-- 3. catalog.tickets : UUID -> VARCHAR(255)
-- ------------------------------------------------------------

ALTER TABLE catalog.tickets
    ALTER COLUMN id       TYPE VARCHAR(255) USING id::text,
    ALTER COLUMN event_id TYPE VARCHAR(255) USING event_id::text;

-- La contrainte UNIQUE (id, event_id) existe déjà en v3,
-- on la recrée si besoin.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_schema = 'catalog'
          AND table_name = 'tickets'
          AND constraint_name = 'ux_tickets_id_event'
    ) THEN
        ALTER TABLE catalog.tickets
            ADD CONSTRAINT ux_tickets_id_event
                UNIQUE (id, event_id);
    END IF;
END$$;

-- ------------------------------------------------------------
-- 4. social.likes : event_id UUID -> VARCHAR(255)
-- ------------------------------------------------------------

ALTER TABLE social.likes
    ALTER COLUMN event_id TYPE VARCHAR(255) USING event_id::text;

-- ------------------------------------------------------------
-- 5. booking.bookings : event_id UUID -> VARCHAR(255)
-- ------------------------------------------------------------

ALTER TABLE booking.bookings
    ALTER COLUMN event_id TYPE VARCHAR(255) USING event_id::text;

-- ------------------------------------------------------------
-- 6. booking.booking_lines : event_id / ticket_id UUID -> VARCHAR(255)
-- ------------------------------------------------------------

ALTER TABLE booking.booking_lines
    ALTER COLUMN event_id  TYPE VARCHAR(255) USING event_id::text,
    ALTER COLUMN ticket_id TYPE VARCHAR(255) USING ticket_id::text;

-- ------------------------------------------------------------
-- 7. Recréation des contraintes de clé étrangère
-- ------------------------------------------------------------

-- tickets.event_id -> events.id
ALTER TABLE catalog.tickets
    ADD CONSTRAINT fk_tickets_event
        FOREIGN KEY (event_id)
        REFERENCES catalog.events(id)
        ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_tickets_event_id
    ON catalog.tickets (event_id);

-- likes.event_id -> events.id
ALTER TABLE social.likes
    ADD CONSTRAINT likes_event_id_fkey
        FOREIGN KEY (event_id)
        REFERENCES catalog.events(id)
        ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_likes_event_id
    ON social.likes (event_id);

-- bookings.event_id -> events.id
ALTER TABLE booking.bookings
    ADD CONSTRAINT bookings_event_id_fkey
        FOREIGN KEY (event_id)
        REFERENCES catalog.events(id);

CREATE INDEX IF NOT EXISTS idx_bookings_event_id
    ON booking.bookings (event_id);

-- booking_lines.booking_id,event_id -> bookings.id,event_id
ALTER TABLE booking.booking_lines
    ADD CONSTRAINT fk_booking_lines_booking
        FOREIGN KEY (booking_id, event_id)
        REFERENCES booking.bookings (id, event_id)
        ON DELETE CASCADE;

-- booking_lines.ticket_id,event_id -> tickets.id,event_id
ALTER TABLE booking.booking_lines
    ADD CONSTRAINT fk_booking_lines_ticket
        FOREIGN KEY (ticket_id, event_id)
        REFERENCES catalog.tickets (id, event_id);

CREATE INDEX IF NOT EXISTS idx_booking_lines_booking_id
    ON booking.booking_lines (booking_id);

CREATE INDEX IF NOT EXISTS idx_booking_lines_ticket_id
    ON booking.booking_lines (ticket_id);

CREATE INDEX IF NOT EXISTS idx_booking_lines_event_id
    ON booking.booking_lines (event_id);

COMMIT;
