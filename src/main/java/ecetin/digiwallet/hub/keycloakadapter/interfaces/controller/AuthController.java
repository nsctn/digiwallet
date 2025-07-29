package ecetin.digiwallet.hub.keycloakadapter.interfaces.controller;

import ecetin.digiwallet.hub.keycloakadapter.domain.KeycloakTokenService;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.AuthRequest;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.AuthResponse;
import ecetin.digiwallet.hub.keycloakadapter.interfaces.dto.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API for obtaining access tokens")
public class AuthController {

    private final KeycloakTokenService keycloakTokenService;

    /**
     * Authenticates a user and returns an access token.
     *
     * @param authRequest the authentication request containing username and password
     * @return the authentication response containing the access token and other information
     */
    @PostMapping("/token")
    @Operation(summary = "Obtain access token", description = "Authenticates a user and returns an access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<AuthResponse> getToken(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Authenticating user: {}", authRequest.getUsername());

        AuthResponse response = keycloakTokenService.getToken(authRequest);

        log.info("Authentication successful for user: {}", authRequest.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshTokenRequest the request containing the refresh token
     * @return the authentication response containing the new access token and other information
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refreshes an access token using a refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refresh successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token refresh failed"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Refreshing token");

        AuthResponse response = keycloakTokenService.refreshToken(refreshTokenRequest);

        log.info("Token refresh successful");
        return ResponseEntity.ok(response);
    }
}
