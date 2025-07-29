package ecetin.digiwallet.hub.keycloakadapter.application;

import ecetin.digiwallet.hub.customer.domain.event.CustomerCreatedEvent;
import ecetin.digiwallet.hub.employee.domain.event.EmployeeCreatedEvent;
import ecetin.digiwallet.hub.keycloakadapter.domain.KeycloakService;
import ecetin.digiwallet.hub.keycloakadapter.domain.KeycloakUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Event handler for user-related events.
 * Listens for customer and employee creation events and creates corresponding users in Keycloak.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {

    private final KeycloakService keycloakService;
    
    // Default password for new users
    private static final String DEFAULT_PASSWORD = "admin";
    
    /**
     * Handles customer creation events.
     * Creates a corresponding user in Keycloak with default credentials.
     *
     * @param event the customer created event
     */
    @TransactionalEventListener
    public void handleCustomerCreatedEvent(CustomerCreatedEvent event) {
        log.info("Handling customer created event for customer with ID: {}", event.id());
        
        // Create a username based on TCKN
        String username = "customer_" + event.tckn();
        
        // Create a Keycloak user
        KeycloakUser user = createKeycloakUser(
                username,
                event.name(),
                event.surname(),
                username + "@example.com",
                Map.of()
        );
        
        keycloakService.createUser(user);
    }
    
    /**
     * Handles employee creation events.
     * Creates a corresponding user in Keycloak with default credentials.
     *
     * @param event the employee created event
     */
    @TransactionalEventListener
    public void handleEmployeeCreatedEvent(EmployeeCreatedEvent event) {
        log.info("Handling employee created event for employee with ID: {}", event.id());
        
        // Create a username based on employee ID
        String username = "employee_" + event.employeeId();
        
        // Create a Keycloak user
        KeycloakUser user = createKeycloakUser(
                username,
                event.name(),
                event.surname(),
                username + "@example.com",
                Map.of()
        );
        
        keycloakService.createUser(user);
    }
    
    /**
     * Creates a Keycloak user with the given information and default credentials.
     *
     * @param username the username
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email
     * @param attributes additional attributes
     * @return the created Keycloak user
     */
    private KeycloakUser createKeycloakUser(
            String username,
            String firstName,
            String lastName,
            String email,
            Map<String, List<String>> attributes) {
        
        // Create a credential with the default password
        KeycloakUser.KeycloakCredential credential = KeycloakUser.KeycloakCredential.builder()
                .type("password")
                .value(DEFAULT_PASSWORD)
                .temporary(false)
                .build();
        
        // Create and return the user
        return KeycloakUser.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .enabled(true)
                .emailVerified(true)
                .attributes(attributes)
                .credentials(Collections.singletonList(credential))
                .build();
    }
}