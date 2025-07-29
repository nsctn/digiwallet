package ecetin.digiwallet.hub.keycloakadapter.domain;

import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.AuthRequest;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.AuthResponse;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.RefreshTokenRequest;

/**
 * Service interface for token operations with Keycloak.
 */
public interface KeycloakTokenService {

    /**
     * Obtains an access token from Keycloak.
     *
     * @param authRequest the authentication request containing username and password
     * @return the authentication response containing the access token and other information
     */
    AuthResponse getToken(AuthRequest authRequest);

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshTokenRequest the request containing the refresh token
     * @return the authentication response containing the new access token and other information
     */
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}