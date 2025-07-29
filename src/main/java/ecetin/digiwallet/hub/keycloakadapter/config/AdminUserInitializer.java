package ecetin.digiwallet.hub.keycloakadapter.config;

import ecetin.digiwallet.hub.keycloakadapter.domain.KeycloakService;
import ecetin.digiwallet.hub.keycloakadapter.domain.KeycloakUser;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes an admin user with EMPLOYEE group when the application starts.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

  private final KeycloakService keycloakService;

  @Override
  public void run(ApplicationArguments args) {
    log.info("Initializing admin user...");

    try {
      // Check if admin user already exists
      Optional<KeycloakUser> existingUser = keycloakService.findUserByUsername("admin");

      if (existingUser != null && existingUser.isPresent()) {
        log.info("Admin user already exists, skipping creation");
        return;
      }

      // Create admin user
      KeycloakUser.KeycloakCredential credential = KeycloakUser.KeycloakCredential.builder()
          .type("password")
          .value("admin")
          .temporary(false)
          .build();

      KeycloakUser adminUser = KeycloakUser.builder()
          .username("admin")
          .firstName("Admin")
          .lastName("User")
          .email("admin@example.com")
          .enabled(true)
          .emailVerified(true)
          .attributes(Map.of("isAdmin", List.of("true")))
          .credentials(Collections.singletonList(credential))
          .build();

      keycloakService.createUser(adminUser);
      keycloakService.assignToGroup("admin", "EMPLOYEE");

      log.info("Admin user created successfully with EMPLOYEE role");
    } catch (Exception e) {
      log.error("Error creating admin user: {}", e.getMessage(), e);
    }
  }
}