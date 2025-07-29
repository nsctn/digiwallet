package ecetin.digiwallet.hub.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.UUID;

/**
 * Custom JWT principal that holds both customer ID and employee status.
 * This class extends JwtAuthenticationToken to maintain compatibility with Spring Security.
 */
@Getter
public class CustomJwtPrincipal extends JwtAuthenticationToken {

    private final UUID customerId;
    private final boolean isEmployee;

    /**
     * Creates a new CustomJwtPrincipal with the given JWT token, authorities, customer ID, and employee status.
     *
     * @param jwt The JWT token
     * @param authorities The granted authorities
     * @param customerId The customer ID extracted from the JWT
     * @param isEmployee Whether the user is an employee
     */
    public CustomJwtPrincipal(Jwt jwt, Collection<? extends GrantedAuthority> authorities, 
                             UUID customerId, boolean isEmployee) {
        super(jwt, authorities);
        this.customerId = customerId;
        this.isEmployee = isEmployee;
    }
}