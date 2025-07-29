package ecetin.digiwallet.hub.keycloakadapter.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ecetin.digiwallet.hub.keycloakadapter.domain.KeycloakUser;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeycloakAdminServiceImplTest {

    @Mock
    private UsersResource usersResource;

    @Mock
    private Response response;

    @Mock
    private UserResource userResource;

    @InjectMocks
    private KeycloakAdminServiceImpl keycloakAdminService;

    private KeycloakUser keycloakUser;
    private UserRepresentation userRepresentation;

    @BeforeEach
    void setUp() {
        // Setup KeycloakUser with credential
        KeycloakUser.KeycloakCredential credential = KeycloakUser.KeycloakCredential.builder()
                .type(CredentialRepresentation.PASSWORD)
                .value("password")
                .temporary(true)
                .build();

        keycloakUser = KeycloakUser.builder()
                .id("user-id")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .enabled(true)
                .emailVerified(true)
                .attributes(new HashMap<>())
                .credentials(Collections.singletonList(credential))
                .build();

        // Setup UserRepresentation
        userRepresentation = new UserRepresentation();
        userRepresentation.setId("user-id");
        userRepresentation.setUsername("testuser");
        userRepresentation.setFirstName("Test");
        userRepresentation.setLastName("User");
        userRepresentation.setEmail("test@example.com");
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        userRepresentation.setAttributes(new HashMap<>());
    }

    @Test
    void createUser_Success() throws Exception {
        // Arrange
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        lenient().when(response.getLocation()).thenReturn(new URI("http://keycloak/users/user-id"));

        // Act
        keycloakAdminService.createUser(keycloakUser);

        // Assert
        verify(usersResource).create(any(UserRepresentation.class));
        verify(response).close();
    }

    @Test
    void createUser_Failure() {
        // Arrange
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(400);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            keycloakAdminService.createUser(keycloakUser);
        });
        verify(usersResource).create(any(UserRepresentation.class));
        verify(response).close();
    }

    @Test
    void findUserByUsername_Success() {
        // Arrange
        when(usersResource.search("testuser", true)).thenReturn(Collections.singletonList(userRepresentation));

        // Act
        Optional<KeycloakUser> result = keycloakAdminService.findUserByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("user-id", result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("Test", result.get().getFirstName());
        assertEquals("User", result.get().getLastName());
        assertEquals("test@example.com", result.get().getEmail());
        assertTrue(result.get().isEnabled());
        assertTrue(result.get().isEmailVerified());
        verify(usersResource).search("testuser", true);
    }

    @Test
    void findUserByUsername_UserNotFound() {
        // Arrange
        when(usersResource.search("testuser", true)).thenReturn(Collections.emptyList());

        // Act
        Optional<KeycloakUser> result = keycloakAdminService.findUserByUsername("testuser");

        // Assert
        // The method returns Optional.empty() when user is not found
        assertFalse(result != null && result.isPresent());
        verify(usersResource).search("testuser", true);
    }

    @Test
    void findUserByUsername_Exception() {
        // Arrange
        when(usersResource.search("testuser", true)).thenThrow(new RuntimeException("Keycloak error"));

        // Act
        Optional<KeycloakUser> result = keycloakAdminService.findUserByUsername("testuser");

        // Assert
        assertFalse(result.isPresent());
        verify(usersResource).search("testuser", true);
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        when(usersResource.get("user-id")).thenReturn(userResource);

        // Act
        keycloakAdminService.resetPassword("user-id", "newpassword", true);

        // Assert
        verify(usersResource).get("user-id");
        verify(userResource).resetPassword(any(CredentialRepresentation.class));
    }

    @Test
    void resetPassword_Exception() {
        // Arrange
        when(usersResource.get("user-id")).thenReturn(userResource);
        doThrow(new RuntimeException("Keycloak error")).when(userResource).resetPassword(any(CredentialRepresentation.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            keycloakAdminService.resetPassword("user-id", "newpassword", true);
        });
        verify(usersResource).get("user-id");
        verify(userResource).resetPassword(any(CredentialRepresentation.class));
    }
}
