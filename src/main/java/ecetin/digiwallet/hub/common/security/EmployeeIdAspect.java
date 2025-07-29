package ecetin.digiwallet.hub.common.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Aspect that handles the injection of employee status from the authentication principal
 * into method parameters annotated with @EmployeeId.
 */
@Aspect
@Component
public class EmployeeIdAspect {

    /**
     * Intercepts method calls with @EmployeeId parameters and injects the employee status.
     *
     * @param joinPoint The join point representing the intercepted method call
     * @return The result of the method execution
     * @throws Throwable If an error occurs during method execution
     */
    @Around("execution(* ecetin.digiwallet.hub..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object injectEmployeeId(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        
        boolean isEmployee = isEmployee();
        
        // Check each parameter for @EmployeeId annotation
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof IsEmployee) {
                    // Check if parameter type is Boolean
                    if (parameterTypes[i].equals(Boolean.class) || parameterTypes[i].equals(boolean.class)) {
                        args[i] = isEmployee;
                    }
                }
            }
        }
        
        return joinPoint.proceed(args);
    }
    
    /**
     * Checks if the authenticated user is an employee.
     * 
     * @return true if the authentication principal is a CustomJwtPrincipal and isEmployee is true, false otherwise
     */
    private boolean isEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        if (authentication instanceof CustomJwtPrincipal) {
            return ((CustomJwtPrincipal) authentication).isEmployee();
        }
        
        return false;
    }
}