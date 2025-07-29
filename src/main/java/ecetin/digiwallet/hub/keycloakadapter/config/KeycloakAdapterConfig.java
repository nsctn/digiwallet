package ecetin.digiwallet.hub.keycloakadapter.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the keycloakadapter module.
 * This ensures that the module is properly set up and recognized by Spring.
 * Enables Feign clients for the keycloakadapter module.
 */
@Configuration
@ComponentScan(basePackages = "ecetin.digiwallet.hub.keycloakadapter")
@EnableFeignClients(basePackages = "ecetin.digiwallet.hub.keycloakadapter.infrastructure.client")
public class KeycloakAdapterConfig {
    // Configuration is handled through component scanning
}
