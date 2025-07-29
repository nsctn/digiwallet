package ecetin.digiwallet.hub.keycloakadapter.infrastructure;

import ecetin.digiwallet.hub.keycloakadapter.domain.KeycloakTokenService;
import ecetin.digiwallet.hub.keycloakadapter.infrastructure.client.KeycloakTokenClient;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.AuthRequest;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.AuthResponse;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.KeycloakRequestDTO;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementation of the KeycloakTokenService interface.
 * Uses a FeignClient to interact with Keycloak's token endpoint.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakTokenServiceImpl implements KeycloakTokenService {

  private final KeycloakTokenClient keycloakTokenClient;

  @Value("${keycloak.admin-client.client-id}")
  private String clientId;

  @Value("${keycloak.admin-client.client-secret}")
  private String clientSecret;

  @Value("${keycloak.realm}")
  private String realm;

  @Override
  public AuthResponse getToken(AuthRequest authRequest) {
    log.info("Authenticating user: {}", authRequest.getUsername());

    KeycloakRequestDTO requestDTO = KeycloakRequestDTO.builder()
        .grantType("password")
        .clientId(clientId)
        .clientSecret(clientSecret)
        .username(authRequest.getUsername())
        .password(authRequest.getPassword())
        .build();

    AuthResponse response = keycloakTokenClient.getToken(requestDTO, realm);

    log.info("Authentication successful for user: {}", authRequest.getUsername());
    return response;
  }

  @Override
  public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
    log.info("Refreshing token");

    KeycloakRequestDTO requestDTO = KeycloakRequestDTO.builder()
        .grantType("refresh_token")
        .clientId(clientId)
        .clientSecret(clientSecret)
        .refreshToken(refreshTokenRequest.getRefreshToken())
        .build();

    AuthResponse response = keycloakTokenClient.getToken(requestDTO, realm);

    log.info("Token refresh successful");
    return response;
  }
}
