package ecetin.digiwallet.hub.common.security;

import io.swagger.v3.oas.annotations.Parameter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark method parameters that should be injected with the customer ID from the JWT token.
 * This annotation can be used on controller method parameters of type UUID.
 * The parameter is hidden from Swagger UI as it is automatically injected from the JWT token.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(hidden = true)
public @interface CustomerId {
}