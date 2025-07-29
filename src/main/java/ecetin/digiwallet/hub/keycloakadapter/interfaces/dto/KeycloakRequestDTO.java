package ecetin.digiwallet.hub.keycloakadapter.interfaces.dto;

import feign.form.FormProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Keycloak token requests.
 * Used to convert to form parameters for the token endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakRequestDTO {

    @FormProperty("grant_type")
    private String grantType;

    @FormProperty("client_id")
    private String clientId;

    @FormProperty("client_secret")
    private String clientSecret;

    @FormProperty("username")
    private String username;

    @FormProperty("password")
    private String password;

    @FormProperty("refresh_token")
    private String refreshToken;
}