package ecetin.digiwallet.hub.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import ecetin.digiwallet.hub.common.security.CustomJwtPrincipal;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerIdAspectTest {

    @InjectMocks
    private CustomerIdAspect customerIdAspect;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    private final UUID testCustomerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void extractCustomerId_shouldReturnCustomerId_whenJwtContainsValidCustomerId() {
        // Arrange
        JwtAuthenticationToken jwtAuth = new JwtAuthenticationToken(jwt);
        when(securityContext.getAuthentication()).thenReturn(jwtAuth);
        when(jwt.getClaimAsString("sub")).thenReturn(testCustomerId.toString());

        // Act
        UUID result = customerIdAspect.extractCustomerId();

        // Assert
        assertEquals(testCustomerId, result);
    }

    @Test
    void extractCustomerId_shouldReturnNull_whenAuthenticationIsNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        UUID result = customerIdAspect.extractCustomerId();

        // Assert
        assertNull(result);
    }

    @Test
    void extractCustomerId_shouldReturnNull_whenJwtDoesNotContainCustomerId() {
        // Arrange
        JwtAuthenticationToken jwtAuth = new JwtAuthenticationToken(jwt);
        when(securityContext.getAuthentication()).thenReturn(jwtAuth);
        when(jwt.getClaimAsString("sub")).thenReturn(null);

        // Act
        UUID result = customerIdAspect.extractCustomerId();

        // Assert
        assertNull(result);
    }

    @Test
    void isEmployee_shouldReturnTrue_whenUserHasEmployeeRole() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn(
                (Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));

        // Act
        boolean result = customerIdAspect.isEmployee();

        // Assert
        assertTrue(result);
    }

    @Test
    void isEmployee_shouldReturnFalse_whenUserDoesNotHaveEmployeeRole() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn(
                (Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // Act
        boolean result = customerIdAspect.isEmployee();

        // Assert
        assertFalse(result);
    }

    @Test
    void isEmployee_shouldReturnFalse_whenAuthenticationIsNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        boolean result = customerIdAspect.isEmployee();

        // Assert
        assertFalse(result);
    }
    
    @Test
    void extractCustomerId_shouldReturnCustomerId_whenUsingCustomJwtPrincipal() {
        // Arrange
        CustomJwtPrincipal customJwtPrincipal = mock(CustomJwtPrincipal.class);
        when(securityContext.getAuthentication()).thenReturn(customJwtPrincipal);
        when(customJwtPrincipal.getCustomerId()).thenReturn(testCustomerId);
        
        // Act
        UUID result = customerIdAspect.extractCustomerId();
        
        // Assert
        assertEquals(testCustomerId, result);
        verify(customJwtPrincipal).getCustomerId();
    }
    
    @Test
    void isEmployee_shouldReturnTrue_whenUsingCustomJwtPrincipalAndIsEmployee() {
        // Arrange
        CustomJwtPrincipal customJwtPrincipal = mock(CustomJwtPrincipal.class);
        when(securityContext.getAuthentication()).thenReturn(customJwtPrincipal);
        when(customJwtPrincipal.isEmployee()).thenReturn(true);
        
        // Act
        boolean result = customerIdAspect.isEmployee();
        
        // Assert
        assertTrue(result);
        verify(customJwtPrincipal).isEmployee();
    }
    
    @Test
    void isEmployee_shouldReturnFalse_whenUsingCustomJwtPrincipalAndIsNotEmployee() {
        // Arrange
        CustomJwtPrincipal customJwtPrincipal = mock(CustomJwtPrincipal.class);
        when(securityContext.getAuthentication()).thenReturn(customJwtPrincipal);
        when(customJwtPrincipal.isEmployee()).thenReturn(false);
        
        // Act
        boolean result = customerIdAspect.isEmployee();
        
        // Assert
        assertFalse(result);
        verify(customJwtPrincipal).isEmployee();
    }
}