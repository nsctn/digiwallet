package ecetin.digiwallet.hub.keycloakadapter.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication responses.
 * Contains the access token, refresh token, and other relevant information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response")
public class AuthResponse {

    @JsonProperty("access_token")
    @Schema(description = "JWT access token", example = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJfT3...")
    private String accessToken;

    @JsonProperty("expires_in")
    @Schema(description = "Token expiration time in seconds", example = "300")
    private Integer expiresIn;

    @JsonProperty("refresh_expires_in")
    @Schema(description = "Refresh token expiration time in seconds", example = "1800")
    private Integer refreshExpiresIn;

    @JsonProperty("refresh_token")
    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @JsonProperty("token_type")
    @Schema(description = "Type of token", example = "Bearer")
    private String tokenType;

    @JsonProperty("not-before-policy")
    @Schema(description = "Not before policy time", example = "0")
    private Integer notBeforePolicy;

    @JsonProperty("session_state")
    @Schema(description = "Session state", example = "58ba5a05-1675-4e15-a383-d2f3a6b7a3e4")
    private String sessionState;

    @Schema(description = "Scope of the token", example = "profile email")
    private String scope;
}