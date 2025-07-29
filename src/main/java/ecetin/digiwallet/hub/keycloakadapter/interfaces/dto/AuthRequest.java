package ecetin.digiwallet.hub.keycloakadapter.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication requests.
 * Contains username and password for authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication request")
public class AuthRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username for authentication", example = "customer_12345678901")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password for authentication", example = "password")
    private String password;
}