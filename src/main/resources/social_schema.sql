-- Création du schéma isolé pour le Bounded Context "Social"
CREATE SCHEMA IF NOT EXISTS social;

-- =============================================
-- Table LIKES
-- Lien entre un utilisateur (Keycloak) et un événement
-- =============================================
CREATE TABLE IF NOT EXISTS social.likes (
    user_id     VARCHAR(255) NOT NULL, -- ID utilisateur (sub du token JWT)
    event_id    VARCHAR(255) NOT NULL, -- ID de l'événement (référence catalog)
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Clé primaire composée (un user ne peut liker qu'une fois un event)
    CONSTRAINT pk_likes PRIMARY KEY (user_id, event_id),

    -- Clé étrangère vers le catalogue (optionnel mais recommandé pour la cohérence)
    -- Si l'événement est supprimé, le like disparaît
    CONSTRAINT fk_likes_event
        FOREIGN KEY (event_id)
        REFERENCES catalog.events(id)
        ON DELETE CASCADE
);

-- Index pour compter rapidement les likes d'un événement
CREATE INDEX IF NOT EXISTS idx_likes_event_id ON social.likes(event_id);

-- Index pour retrouver rapidement les likes d'un utilisateur (Mes favoris)
CREATE INDEX IF NOT EXISTS idx_likes_user_id ON social.likes(user_id);