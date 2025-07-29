package ecetin.digiwallet.hub.keycloakadapter.infrastructure.client;

import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.AuthResponse;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.KeycloakRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for interacting with Keycloak's token endpoint.
 * This client is used to obtain and refresh access tokens.
 */
@FeignClient(name = "keycloak", url = "${keycloak.auth-server-url}")
public interface KeycloakTokenClient {

    /**
     * Obtains an access token using the password grant type.
     *
     * @param requestDTO the request DTO containing grant_type, client_id, username, and password
     * @param realm the Keycloak realm
     * @return the authentication response containing the access token and other information
     */
    @PostMapping(path = "/realms/{realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AuthResponse getToken(
            @RequestBody KeycloakRequestDTO requestDTO,
            @PathVariable("realm") String realm
    );
}
