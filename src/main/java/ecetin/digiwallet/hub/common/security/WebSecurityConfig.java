package ecetin.digiwallet.hub.common.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  private String jwkSetUri;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authz ->
                authz
                    .requestMatchers("/api/v1/wallets/**")
                    .authenticated()
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs",
                        "/api-docs/**",
                        "/h2-console/**")
                    .permitAll()
                    .anyRequest()
                    .permitAll())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

    return http.build();
  }

  @Bean
  public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    return jwt -> {
      // realm roles (optional, not used in your case)
      JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
      grantedAuthoritiesConverter.setAuthorityPrefix(""); // optional
      Collection<GrantedAuthority> authorities = grantedAuthoritiesConverter.convert(jwt);

      // resource_access → digiwallet-client → roles
      Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
      if (resourceAccess != null && resourceAccess.containsKey("digiwallet-client")) {
        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("digiwallet-client");
        if (clientAccess.containsKey("roles")) {
          List<String> roles = (List<String>) clientAccess.get("roles");

          List<GrantedAuthority> clientAuthorities = roles.stream()
              .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
              .collect(Collectors.toList());

          authorities.addAll(clientAuthorities);
        }
      }

      UUID customerId = extractCustomerId(jwt);
      boolean isEmployee = authorities.stream()
          .anyMatch(
              a -> a.getAuthority().equals("ROLE_EMPLOYEE:VIEW") || a.getAuthority().equals("ROLE_EMPLOYEE:CREATE"));

      return new CustomJwtPrincipal(jwt, authorities, customerId, isEmployee);
    };
  }


  /**
   * Extracts the customer ID from the JWT token.
   *
   * @param jwt The JWT token
   * @return The customer ID as UUID or null if not found or not a valid UUID
   */
  private UUID extractCustomerId(Jwt jwt) {
    String customerId = jwt.getClaimAsString("sub");
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

  /**
   * Creates a JwtDecoder bean that uses the JWK Set URI from application.yml.
   * This bean is required by Spring Security's OAuth2 Resource Server to decode and validate JWT tokens.
   *
   * @return A configured JwtDecoder
   */
  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
  }
}
