-- ============================================
-- SCHEMAS = Bounded Contexts
-- ============================================

CREATE SCHEMA IF NOT EXISTS catalog;
CREATE SCHEMA IF NOT EXISTS social;
CREATE SCHEMA IF NOT EXISTS booking;

-- ============================================
-- TYPE ENUM POUR LES STATUTS DE RÉSERVATION
-- (dans le schema "booking")
-- ============================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'booking_status'
          AND n.nspname = 'booking'
    ) THEN
        CREATE TYPE booking.booking_status AS ENUM (
          'requested',
          'reserved',
          'rejected',
          'awaiting-payment',
          'expired',
          'confirmed'
        );
    END IF;
END$$;

-- ============================================
-- CATALOG.EVENTS
-- ============================================

CREATE TABLE IF NOT EXISTS catalog.events (
    id              UUID PRIMARY KEY,                -- id de l'événement (Strapi)
    title           VARCHAR(255)       NOT NULL,
    description     TEXT               NOT NULL,
    cover_url       TEXT               NOT NULL,     -- URL Cloudinary

    venue_name      VARCHAR(255)       NOT NULL,
    venue_address   VARCHAR(512)       NOT NULL,
    venue_latitude  DOUBLE PRECISION   NOT NULL,
    venue_longitude DOUBLE PRECISION   NOT NULL,

    start_at        TIMESTAMPTZ        NOT NULL,     -- pour /events/discover & /events/:id

    created_at      TIMESTAMPTZ        NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ        NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_events_start_at
    ON catalog.events (start_at);

CREATE INDEX IF NOT EXISTS idx_events_venue_coords
    ON catalog.events (venue_latitude, venue_longitude);

-- ============================================
-- CATALOG.TICKETS
-- ============================================

CREATE TABLE IF NOT EXISTS catalog.tickets (
    id              UUID PRIMARY KEY,                -- id du ticket (Strapi)
    event_id        UUID                NOT NULL
                        REFERENCES catalog.events(id) ON DELETE CASCADE,
    name            VARCHAR(255)        NOT NULL,    -- "Early Bird", "VIP", ...
    price_cents     INTEGER             NOT NULL,    -- prix en centimes
    quantity_total  INTEGER             NOT NULL,    -- stock initial

    created_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW(),

    -- pour pouvoir faire une FK composite (ticket_id, event_id)
    CONSTRAINT ux_tickets_id_event UNIQUE (id, event_id)
);

CREATE INDEX IF NOT EXISTS idx_tickets_event_id
    ON catalog.tickets (event_id);

-- ============================================
-- SOCIAL.LIKES
-- ============================================

CREATE TABLE IF NOT EXISTS social.likes (
    user_id    TEXT           NOT NULL,             -- id Keycloak (sub du JWT)
    event_id   UUID           NOT NULL
                    REFERENCES catalog.events(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_likes PRIMARY KEY (user_id, event_id)
);

CREATE INDEX IF NOT EXISTS idx_likes_event_id
    ON social.likes (event_id);

-- ============================================
-- BOOKING.BOOKINGS
-- ============================================

CREATE TABLE IF NOT EXISTS booking.bookings (
    id              UUID                    PRIMARY KEY,        -- id de la réservation
    user_id         TEXT                    NOT NULL,           -- user Keycloak
    event_id        UUID                    NOT NULL
                        REFERENCES catalog.events(id),          -- NO ACTION si des bookings existent

    booked_at       TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    status          booking.booking_status  NOT NULL,

    total_cents     INTEGER                 NOT NULL,
    fees_cents      INTEGER                 NOT NULL DEFAULT 0,

    client_secret   TEXT,                                   -- Stripe clientSecret (optionnel)

    created_at      TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ             NOT NULL DEFAULT NOW(),

    -- pour lier (booking_id, event_id) dans booking_lines
    CONSTRAINT ux_bookings_id_event UNIQUE (id, event_id)
);

CREATE INDEX IF NOT EXISTS idx_bookings_user_id
    ON booking.bookings (user_id);

CREATE INDEX IF NOT EXISTS idx_bookings_event_id
    ON booking.bookings (event_id);

CREATE INDEX IF NOT EXISTS idx_bookings_status
    ON booking.bookings (status);

-- ============================================
-- BOOKING.BOOKING_LINES
-- ============================================

CREATE TABLE IF NOT EXISTS booking.booking_lines (
    id              UUID            PRIMARY KEY,        -- id de la ligne
    booking_id      UUID            NOT NULL,
    ticket_id       UUID            NOT NULL,
    event_id        UUID            NOT NULL,           -- même event que booking & ticket

    name            VARCHAR(255)    NOT NULL,           -- snapshot nom ticket
    quantity        INTEGER         NOT NULL,
    price_cents     INTEGER         NOT NULL,           -- snapshot prix en centimes

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    -- 1) garantit que (booking_id, event_id) correspond à une réservation existante
    CONSTRAINT fk_booking_lines_booking
        FOREIGN KEY (booking_id, event_id)
        REFERENCES booking.bookings (id, event_id)
        ON DELETE CASCADE,

    -- 2) garantit que (ticket_id, event_id) correspond à un ticket de CE MÊME event
    CONSTRAINT fk_booking_lines_ticket
        FOREIGN KEY (ticket_id, event_id)
        REFERENCES catalog.tickets (id, event_id)
);

CREATE INDEX IF NOT EXISTS idx_booking_lines_booking_id
    ON booking.booking_lines (booking_id);

CREATE INDEX IF NOT EXISTS idx_booking_lines_ticket_id
    ON booking.booking_lines (ticket_id);

CREATE INDEX IF NOT EXISTS idx_booking_lines_event_id
    ON booking.booking_lines (event_id);
