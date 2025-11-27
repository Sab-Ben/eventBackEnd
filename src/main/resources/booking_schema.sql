-- Création du schéma isolé pour le Bounded Context "Booking"
CREATE SCHEMA IF NOT EXISTS booking;

-- =============================================
-- 1. Table RESERVATIONS
-- Aggregate Root pour les réservations
-- =============================================
CREATE TABLE IF NOT EXISTS booking.reservations (
    id              VARCHAR(255) PRIMARY KEY,
    
    -- Références (ID seulement, pas de FK vers autres BC)
    user_id         VARCHAR(255) NOT NULL,
    event_id        VARCHAR(255) NOT NULL,
    
    -- Statut de la réservation
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    
    -- Montant total en centimes
    total_amount    INTEGER NOT NULL,
    
    -- Timestamps
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at      TIMESTAMPTZ NOT NULL,
    confirmed_at    TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    -- Contrainte sur le statut
    CONSTRAINT chk_reservation_status 
        CHECK (status IN ('PENDING', 'CONFIRMED', 'EXPIRED', 'CANCELLED'))
);

-- Index pour les queries
CREATE INDEX IF NOT EXISTS idx_reservations_user_id ON booking.reservations(user_id);
CREATE INDEX IF NOT EXISTS idx_reservations_status ON booking.reservations(status);
CREATE INDEX IF NOT EXISTS idx_reservations_expires_at ON booking.reservations(expires_at);
CREATE INDEX IF NOT EXISTS idx_reservations_user_status 
    ON booking.reservations(user_id, status);


-- =============================================
-- 2. Table RESERVATION_ITEMS
-- Value Objects liés à une réservation
-- =============================================
CREATE TABLE IF NOT EXISTS booking.reservation_items (
    id              VARCHAR(255) PRIMARY KEY,
    
    -- Lien vers la réservation parent
    reservation_id  VARCHAR(255) NOT NULL,
    
    -- Référence vers le ticket du Catalog BC (copie des données)
    ticket_id       VARCHAR(255) NOT NULL,
    ticket_name     VARCHAR(255) NOT NULL,
    
    -- Prix et quantité (snapshot au moment de la réservation)
    unit_price      INTEGER NOT NULL,  -- En centimes
    quantity        INTEGER NOT NULL,
    
    -- Timestamps
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    -- Clé étrangère : Si la réservation est supprimée, les items aussi
    CONSTRAINT fk_items_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES booking.reservations(id)
        ON DELETE CASCADE,
    
    -- Contraintes de validation
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_price_non_negative CHECK (unit_price >= 0)
);

-- Index pour récupérer rapidement les items d'une réservation
CREATE INDEX IF NOT EXISTS idx_reservation_items_reservation_id 
    ON booking.reservation_items(reservation_id);
