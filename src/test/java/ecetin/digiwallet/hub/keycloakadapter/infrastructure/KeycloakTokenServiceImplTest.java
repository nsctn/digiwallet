package ecetin.digiwallet.hub.keycloakadapter.infrastructure;

import ecetin.digiwallet.hub.keycloakadapter.infrastructure.client.KeycloakTokenClient;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.AuthRequest;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.AuthResponse;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.KeycloakRequestDTO;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.RefreshTokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakTokenServiceImplTest {

    @Mock
    private KeycloakTokenClient keycloakTokenClient;

    @InjectMocks
    private KeycloakTokenServiceImpl keycloakTokenService;

    @Captor
    private ArgumentCaptor<KeycloakRequestDTO> requestCaptor;

    private final String clientId = "test-client-id";
    private final String clientSecret = "test-client-secret";
    private final String realm = "test-realm";

    @BeforeEach
    void setUp() {
        // Set the private fields using ReflectionTestUtils since they're normally set by @Value
        ReflectionTestUtils.setField(keycloakTokenService, "clientId", clientId);
        ReflectionTestUtils.setField(keycloakTokenService, "clientSecret", clientSecret);
        ReflectionTestUtils.setField(keycloakTokenService, "realm", realm);
    }

    @Test
    void getToken_ShouldReturnAuthResponse() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setAccessToken("access-token");
        expectedResponse.setRefreshToken("refresh-token");
        expectedResponse.setExpiresIn(300);
        expectedResponse.setRefreshExpiresIn(1800);
        expectedResponse.setTokenType("bearer");

        when(keycloakTokenClient.getToken(any(KeycloakRequestDTO.class), eq(realm))).thenReturn(expectedResponse);

        // Act
        AuthResponse result = keycloakTokenService.getToken(authRequest);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.getAccessToken(), result.getAccessToken());
        assertEquals(expectedResponse.getRefreshToken(), result.getRefreshToken());
        assertEquals(expectedResponse.getExpiresIn(), result.getExpiresIn());
        assertEquals(expectedResponse.getRefreshExpiresIn(), result.getRefreshExpiresIn());
        assertEquals(expectedResponse.getTokenType(), result.getTokenType());

        // Verify the request DTO was built correctly
        verify(keycloakTokenClient).getToken(requestCaptor.capture(), eq(realm));
        KeycloakRequestDTO capturedRequest = requestCaptor.getValue();
        assertEquals("password", capturedRequest.getGrantType());
        assertEquals(clientId, capturedRequest.getClientId());
        assertEquals(clientSecret, capturedRequest.getClientSecret());
        assertEquals(authRequest.getUsername(), capturedRequest.getUsername());
        assertEquals(authRequest.getPassword(), capturedRequest.getPassword());
    }

    @Test
    void refreshToken_ShouldReturnAuthResponse() {
        // Arrange
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("refresh-token");

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setAccessToken("new-access-token");
        expectedResponse.setRefreshToken("new-refresh-token");
        expectedResponse.setExpiresIn(300);
        expectedResponse.setRefreshExpiresIn(1800);
        expectedResponse.setTokenType("bearer");

        when(keycloakTokenClient.getToken(any(KeycloakRequestDTO.class), eq(realm))).thenReturn(expectedResponse);

        // Act
        AuthResponse result = keycloakTokenService.refreshToken(refreshTokenRequest);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.getAccessToken(), result.getAccessToken());
        assertEquals(expectedResponse.getRefreshToken(), result.getRefreshToken());
        assertEquals(expectedResponse.getExpiresIn(), result.getExpiresIn());
        assertEquals(expectedResponse.getRefreshExpiresIn(), result.getRefreshExpiresIn());
        assertEquals(expectedResponse.getTokenType(), result.getTokenType());

        // Verify the request DTO was built correctly
        verify(keycloakTokenClient).getToken(requestCaptor.capture(), eq(realm));
        KeycloakRequestDTO capturedRequest = requestCaptor.getValue();
        assertEquals("refresh_token", capturedRequest.getGrantType());
        assertEquals(clientId, capturedRequest.getClientId());
        assertEquals(clientSecret, capturedRequest.getClientSecret());
        assertEquals(refreshTokenRequest.getRefreshToken(), capturedRequest.getRefreshToken());
    }
}