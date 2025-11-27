package com.example.eventbackend.shared.config;


import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration et initialisation du moteur de recherche MeiliSearch.
 * <p>
 * Cette classe a la responsabilité de connecter l'application au serveur MeiliSearch
 * et, surtout, de <strong>configurer les règles de l'index</strong> au démarrage.
 * </p>
 */
@Configuration
public class MeiliConfig {

    @Value("${meilisearch.host}")
    private String host;

    @Value("${meilisearch.api-key}")
    private String apiKey;

    /**
     * Crée le bean Client pour communiquer avec l'instance MeiliSearch.
     *
     * @return Une instance authentifiée du client SDK MeiliSearch.
     */
    @Bean
    public Client meilisearchClient() {
        return new Client(new Config(host, apiKey));
    }

    /**
     * Script de configuration exécuté automatiquement au démarrage de l'application.
     * <p>
     * Ce runner s'assure que l'index "events" possède les bons paramétrages pour
     * les fonctionnalités de recherche avancée.
     * </p>
     * <p>
     * <strong>Pourquoi c'est vital ?</strong>
     * Par défaut, MeiliSearch ne permet pas de trier ou de filtrer sur des champs
     * s'ils ne sont pas explicitement déclarés. Sans cette configuration :
     * <ul>
     * <li>Le tri géographique ({@code _geo}) échouerait.</li>
     * <li>Le filtre par rayon ({@code radius}) échouerait.</li>
     * <li>Le tri par popularité ({@code likedCount}) échouerait.</li>
     * </ul>
     * </p>
     *
     * @param client Le client injecté pour effectuer les mises à jour.
     * @return Le runner exécutable par Spring Boot.
     */
    @Bean
    public CommandLineRunner configureMeiliIndex(Client client) {
        return args -> {
            Index index = client.index("events");

            index.updateFilterableAttributesSettings(new String[]{
                    "_geo", "lowestPrice", "startAt", "id"
            });

            index.updateSortableAttributesSettings(new String[]{
                    "_geo", "likedCount", "startAt"
            });

            System.out.println("Index Meilisearch 'events' configuré !");
        };
    }
}