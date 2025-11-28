package com.example.eventbackend.shared.config;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de l'infrastructure CQRS / Mediator (Pipelinr).
 * <p>
 * Cette classe est responsable de l'intégration entre le conteneur d'injection de dépendances
 * de Spring et la librairie <strong>Pipelinr</strong>.
 * </p>
 * <p>
 * <strong>Mécanisme :</strong>
 * Elle scanne automatiquement le contexte Spring pour trouver tous les Beans qui implémentent
 * {@link Command.Handler}, {@link Notification.Handler} et {@link Command.Middleware}.
 * Elle les injecte ensuite dans l'instance {@link Pipeline} qui sera utilisée par les contrôleurs.
 * </p>
 */
@Configuration
public class PipelineRConfig {

    /**
     * Fabrique le bean {@link Pipeline} principal.
     * <p>
     * C'est cette instance qui est injectée dans les contrôleurs (ex: {@code EventController}).
     * Lorsqu'on appelle {@code pipeline.send(command)}, Pipelinr parcourt la liste des handlers
     * enregistrés ici pour trouver celui qui correspond au type de la commande.
     * </p>
     *
     * @param commandHandlers      Provider Spring fournissant le flux de tous les gestionnaires de commandes (Command Handlers) détectés (annotés @Component).
     * @param notificationHandlers Provider Spring fournissant le flux de tous les gestionnaires de notifications (si utilisés).
     * @param middlewares          Provider Spring fournissant le flux des middlewares (ex: Logging, Transaction, Validation) à exécuter avant/après les handlers.
     * @return Une instance de {@link Pipelinr} entièrement configurée et consciente du contexte Spring.
     */
    @Bean
    public Pipeline pipeline(ObjectProvider<Command.Handler> commandHandlers,
                             ObjectProvider<Notification.Handler> notificationHandlers,
                             ObjectProvider<Command.Middleware> middlewares) {
        return new Pipelinr()
                .with(() -> commandHandlers.stream())
                .with(() -> notificationHandlers.stream())
                .with(() -> middlewares.orderedStream());
    }
}
