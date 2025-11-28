-- Création du schéma isolé pour le Bounded Context "Catalog"
CREATE SCHEMA IF NOT EXISTS catalog;

-- =============================================
-- 1. Table EVENTS
-- Contient les infos descriptives et le Venue
-- =============================================
CREATE TABLE IF NOT EXISTS catalog.events (
    -- ID String pour compatibilité UUID ou ID externe (Strapi)
                                              id              VARCHAR(255) PRIMARY KEY,

    -- Informations de base
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    cover_url       TEXT,           -- Correspond au champ JSON "cover"

-- Gestion du Venue (Aplatissement de l'objet Venue)
    venue_name      VARCHAR(255) NOT NULL,
    venue_address   VARCHAR(512) NOT NULL, -- Correspond au champ JSON "adresse"
    venue_latitude  DOUBLE PRECISION NOT NULL,
    venue_longitude DOUBLE PRECISION NOT NULL,

    -- Date et Heure (ISO 8601 via TIMESTAMPTZ)
    start_at        TIMESTAMPTZ NOT NULL,

    -- Audit
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

-- Index pour optimiser le tri par date (endpoint /events/discover)
CREATE INDEX IF NOT EXISTS idx_events_start_at ON catalog.events(start_at);

-- Index pour optimiser la recherche géographique (si faite en SQL direct, sinon utile pour l'export Meili)
CREATE INDEX IF NOT EXISTS idx_events_geo ON catalog.events(venue_latitude, venue_longitude);


-- =============================================
-- 2. Table TICKETS
-- Nécessaire pour calculer "lowestPrice" et "isSoldOut"
-- =============================================
CREATE TABLE IF NOT EXISTS catalog.tickets (
                                               id              VARCHAR(255) PRIMARY KEY,

    -- Lien vers l'événement parent
    event_id        VARCHAR(255) NOT NULL,

    -- Détails du ticket
    name            VARCHAR(255) NOT NULL, -- ex: "Standard", "VIP"
    price           DOUBLE PRECISION NOT NULL,  -- Prix en euros (ex: 12.50)
    quantity_total  INTEGER NOT NULL,      -- Stock initial

    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Clé étrangère : Si l'event est supprimé, ses tickets aussi
    CONSTRAINT fk_tickets_event
    FOREIGN KEY (event_id)
    REFERENCES catalog.events(id)
    ON DELETE CASCADE
    );

-- Index pour récupérer rapidement les tickets d'un event (calcul du prix min)
CREATE INDEX IF NOT EXISTS idx_tickets_event_id ON catalog.tickets(event_id);