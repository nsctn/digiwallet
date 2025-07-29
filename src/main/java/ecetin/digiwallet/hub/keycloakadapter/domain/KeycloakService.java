package ecetin.digiwallet.hub.keycloakadapter.domain;

import java.util.Optional;

/**
 * Service interface for interacting with Keycloak.
 */
public interface KeycloakService {

    /**
     * Creates a new user in Keycloak.
     *
     * @param user the user to create
     */
    void createUser(KeycloakUser user);

    /**
     * Finds a user in Keycloak by username.
     *
     * @param username the username to search for
     * @return the user if found, null otherwise
     */
    Optional<KeycloakUser> findUserByUsername(String username);

    /**
     * Resets a user's password in Keycloak.
     *
     * @param userId the ID of the user
     * @param password the new password
     * @param temporary whether the password is temporary
     */
    void resetPassword(String userId, String password, boolean temporary);
}