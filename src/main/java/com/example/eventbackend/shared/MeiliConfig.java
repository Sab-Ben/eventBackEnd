package com.example.eventbackend.shared;


import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeiliConfig {

    @Value("${meilisearch.host}")
    private String host;

    @Value("${meilisearch.api-key}")
    private String apiKey;

    @Bean
    public Client meilisearchClient() {
        return new Client(new Config(host, apiKey));
    }

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