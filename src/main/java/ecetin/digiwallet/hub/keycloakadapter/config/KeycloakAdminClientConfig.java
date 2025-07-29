package ecetin.digiwallet.hub.keycloakadapter.config;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Keycloak Client config
 */
@Configuration
public class KeycloakAdminClientConfig {

    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String authServerUrl;

    @Value("${keycloak.admin-client.client-secret:admin}")
    private String clientSecret;

    @Value("${keycloak.admin-client.client-id:digiwallet-client}")
    private String clientId;

    @Value("${keycloak.admin-client.realm:digiwallet}")
    private String realm;

    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
            .serverUrl(authServerUrl)
            .realm(realm)
            .grantType(CLIENT_CREDENTIALS)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build();
    }

    @Bean
    UsersResource usersResource(RealmResource realmResource) {
        return realmResource.users();
    }

    @Bean
    RealmResource getRealmResource(Keycloak keycloak) {
        return keycloak.realm(realm);
    }

    @Bean
    GroupsResource getGroupResource(RealmResource realmResource) {
        return realmResource.groups();
    }
}