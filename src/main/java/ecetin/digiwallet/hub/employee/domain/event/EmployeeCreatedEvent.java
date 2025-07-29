package ecetin.digiwallet.hub.employee.domain.event;

import java.util.UUID;

/**
 * Event that is published when a new employee is created.
 * This event contains the necessary information for creating a corresponding user in Keycloak.
 */
public record EmployeeCreatedEvent(
    UUID id,
    String name,
    String surname,
    String employeeId) {}