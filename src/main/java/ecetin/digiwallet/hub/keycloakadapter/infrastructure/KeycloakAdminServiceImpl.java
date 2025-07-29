package ecetin.digiwallet.hub.keycloakadapter.infrastructure;

import ecetin.digiwallet.hub.keycloakadapter.domain.KeycloakService;
import ecetin.digiwallet.hub.keycloakadapter.domain.KeycloakUser;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAdminServiceImpl implements KeycloakService {

  private final UsersResource usersResource;
  private final GroupsResource groupsResource;

  @Override
  public void createUser(KeycloakUser user) {
    try {
      // Convert KeycloakUser to UserRepresentation
      UserRepresentation userRepresentation = new UserRepresentation();
      userRepresentation.setUsername(user.getUsername());
      userRepresentation.setFirstName(user.getFirstName());
      userRepresentation.setLastName(user.getLastName());
      userRepresentation.setEmail(user.getEmail());
      userRepresentation.setEnabled(user.isEnabled());
      userRepresentation.setEmailVerified(user.isEmailVerified());
      userRepresentation.setAttributes(user.getAttributes());

      // Convert credentials
      if (user.getCredentials() != null && !user.getCredentials().isEmpty()) {
        List<CredentialRepresentation> credentials =
            user.getCredentials().stream()
                .map(this::convertCredential)
                .collect(Collectors.toList());
        userRepresentation.setCredentials(credentials);
      }

      // Create user in the configured realm using userManagementKeycloakClient
      Response response = usersResource.create(userRepresentation);

      if (response.getStatus() == 201) {
        String userId = extractCreatedId(response);
        log.info("Created user in Keycloak: {} with ID: {}", user.getUsername(), userId);
      } else {
        log.error(
            "Failed to create user in Keycloak: {} - Status: {}",
            user.getUsername(),
            response.getStatus());
        response.close();
        throw new RuntimeException("Failed to create user in Keycloak");
      }

      response.close();
    } catch (Exception e) {
      log.error("Error creating user in Keycloak: {}", e.getMessage(), e);
      throw new RuntimeException("Error creating user in Keycloak", e);
    }
  }

  @Override
  public Optional<KeycloakUser> findUserByUsername(String username) {
    try {
      // Find user in the configured realm using userManagementKeycloakClient
      List<UserRepresentation> users = usersResource.search(username, true);

      if (users.isEmpty()) {
        log.info("User not found in Keycloak: {}", username);
        return null;
      }

      UserRepresentation userRepresentation = users.get(0);
      return Optional.of(convertToKeycloakUser(userRepresentation));
    } catch (Exception e) {
      log.error("Error finding user in Keycloak: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }

  @Override
  public void resetPassword(String userId, String password, boolean temporary) {
    try {
      CredentialRepresentation credential = new CredentialRepresentation();
      credential.setType(CredentialRepresentation.PASSWORD);
      credential.setValue(password);
      credential.setTemporary(temporary);

      // Reset password in the configured realm using userManagementKeycloakClient
      usersResource.get(userId).resetPassword(credential);
      log.info("Reset password for user with ID: {}", userId);
    } catch (Exception e) {
      log.error("Error resetting password in Keycloak: {}", e.getMessage(), e);
      throw new RuntimeException("Error resetting password in Keycloak", e);
    }
  }

  @Override
  public void assignToGroup(String username, String groupName) {
    // Get the created user
    Optional<KeycloakUser> createdUser = this.findUserByUsername(username);
    if (createdUser.isEmpty()) {
      log.error("User not found in Keycloak: {}", username);
      return;
    }

    // Member of group to the user
    groupsResource.groups(groupName, 0, 1).stream()
        .findAny()
        .ifPresent(
            group -> {
              String groupId = group.getId();
              usersResource.get(createdUser.get().getId()).joinGroup(groupId);
              log.info("Admin user added to group: {} in Keycloak", groupName);
            });
  }

  // Helper methods

  private CredentialRepresentation convertCredential(KeycloakUser.KeycloakCredential credential) {
    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
    credentialRepresentation.setType(credential.getType());
    credentialRepresentation.setValue(credential.getValue());
    credentialRepresentation.setTemporary(credential.isTemporary());
    return credentialRepresentation;
  }

  private KeycloakUser convertToKeycloakUser(UserRepresentation userRepresentation) {
    return KeycloakUser.builder()
        .id(userRepresentation.getId())
        .username(userRepresentation.getUsername())
        .firstName(userRepresentation.getFirstName())
        .lastName(userRepresentation.getLastName())
        .email(userRepresentation.getEmail())
        .enabled(userRepresentation.isEnabled())
        .emailVerified(userRepresentation.isEmailVerified())
        .attributes(userRepresentation.getAttributes())
        .build();
  }

  private String extractCreatedId(Response response) {
    String locationHeader = response.getHeaderString("Location");
    if (locationHeader != null) {
      return locationHeader.replaceAll(".*/([^/]+)$", "$1");
    }
    return null;
  }
}
