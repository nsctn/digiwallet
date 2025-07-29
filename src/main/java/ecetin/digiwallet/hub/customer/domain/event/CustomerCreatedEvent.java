package ecetin.digiwallet.hub.customer.domain.event;

import java.util.UUID;

/**
 * Event that is published when a new customer is created.
 * This event contains the necessary information for creating a corresponding user in Keycloak.
 */
public record CustomerCreatedEvent(
    UUID id,
    String name,
    String surname,
    String tckn) {}