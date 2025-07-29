package ecetin.digiwallet.hub.common.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Aspect that handles the injection of customer ID from JWT token into method parameters
 * annotated with @CustomerId.
 */
@Aspect
@Component
public class CustomerIdAspect {

    private static final String CUSTOMER_ID_CLAIM = "sub";

    /**
     * Intercepts method calls with @CustomerId parameters and injects the customer ID from JWT.
     *
     * @param joinPoint The join point representing the intercepted method call
     * @return The result of the method execution
     * @throws Throwable If an error occurs during method execution
     */
    @Around("execution(* ecetin.digiwallet.hub..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object injectCustomerId(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        
        UUID customerId = extractCustomerId();
        
        // Check each parameter for @CustomerId annotation
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof CustomerId) {
                    // Check if parameter type is UUID
                    if (parameterTypes[i].equals(UUID.class)) {
                        args[i] = customerId;
                    }
                }
            }
        }
        
        return joinPoint.proceed(args);
    }
    
    /**
     * Extracts the customer ID from the authentication principal.
     * 
     * @return The customer ID as UUID or null if not found
     */
    UUID extractCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        
        if (authentication instanceof CustomJwtPrincipal) {
            return ((CustomJwtPrincipal) authentication).getCustomerId();
        }
        
        // Fallback to the old method for backward compatibility
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            String customerId = jwt.getClaimAsString(CUSTOMER_ID_CLAIM);
            
            if (customerId == null || customerId.isEmpty()) {
                return null;
            }
            
            try {
                return UUID.fromString(customerId);
            } catch (IllegalArgumentException e) {
                // If the subject is not a valid UUID, return null
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Checks if the authenticated user is an employee.
     * 
     * @return true if the user is an employee, false otherwise
     */
    boolean isEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        if (authentication instanceof CustomJwtPrincipal) {
            return ((CustomJwtPrincipal) authentication).isEmployee();
        }
        
        // Fallback to the old method for backward compatibility
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
    }
}