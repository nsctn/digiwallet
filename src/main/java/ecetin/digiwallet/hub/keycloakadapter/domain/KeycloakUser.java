package ecetin.digiwallet.hub.keycloakadapter.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Represents a user in Keycloak.
 * This is a simplified representation of the Keycloak user model.
 */
@Getter
@Builder
public class KeycloakUser {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
    private boolean emailVerified;
    private Map<String, List<String>> attributes;
    private List<KeycloakCredential> credentials;

    /**
     * Represents a credential in Keycloak.
     */
    @Getter
    @Builder
    public static class KeycloakCredential {
        private String type;
        private String value;
        private boolean temporary;
    }
}