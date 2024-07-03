import jakarta.ws.rs.ProcessingException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.userprofile.config.UPAttribute;
import org.keycloak.representations.userprofile.config.UPConfig;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This is a script to initialize Keycloak with the necessary configuration for the application.
 * It creates a new realm, two clients (frontend and backend), and adds some custom attributes to the user profile.
 * The script will remove the realm if it already exists.
 */
public class InitKeycloak {

    private static final String REALMNAME = "dcm";

    public static void main(String[] args) {

        warnUserBeforeExecution();

        try (Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build()) {

            keycloak.serverInfo().getInfo();
            System.out.println("Connected to Keycloak : realm master");

            removeExistingRealmIfNecessary(keycloak);
            createNewRealm(keycloak);
            System.out.println("Realm created successfully");

            createFrontendClient(keycloak);
            System.out.println("Frontend client created successfully");

            createBackendClient(keycloak);
            System.out.println("Backend client created successfully");

            configUserProfileAttributes(keycloak);
            System.out.println("User profile config updated successfully");

            createRoles(keycloak);
            System.out.println("Roles created successfully");

            System.out.println("Keycloak initialization completed successfully");

        } catch (ProcessingException e) {
            System.out.println("Error connecting to Keycloak");
            System.out.println(
                    "Please make sure Keycloak is running on http://localhost:8080");
        }
    }

    private static void warnUserBeforeExecution() {
        System.out.println(
                "Warning : This script will remove the realm '" + REALMNAME + "' if it already exists");
        System.out.println("Enter Y to continue or any other key to exit");
        try (Scanner scanner = new Scanner(System.in)) {
            if (!scanner.nextLine().equalsIgnoreCase("Y")) {
                System.out.println("Exiting...");
                System.exit(0);
            }
        }
    }

    private static void removeExistingRealmIfNecessary(Keycloak keycloak) {
        if (keycloak.realms()
                .findAll()
                .stream()
                .anyMatch(realmRepresentation -> realmRepresentation.getRealm()
                        .equals(REALMNAME))) {
            System.out.println("Realm already exists");
            //System.exit(0);
            keycloak.realm(REALMNAME).remove();
            System.out.println("Realm removed successfully");
        }
    }

    private static void createNewRealm(Keycloak keycloak) {
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(REALMNAME);
        realm.setEnabled(true);
        realm.setRegistrationEmailAsUsername(true);
        keycloak.realms().create(realm);
    }

    private static void createFrontendClient(Keycloak keycloak) {
        ClientRepresentation frontendClient = new ClientRepresentation();
        frontendClient.setClientId("frontend-" + REALMNAME);
        frontendClient.setName("frontend");
        frontendClient.setBaseUrl("http://localhost:4200");
        frontendClient.setRedirectUris(List.of("http://localhost:4200/*"));
        frontendClient.setAttributes(
                Map.of("post.logout.redirect.uris", "http://localhost:4200/*"));
        frontendClient.setWebOrigins(List.of("*"));
        frontendClient.setPublicClient(true);
        frontendClient.setDirectAccessGrantsEnabled(true);
        keycloak.realm(REALMNAME).clients().create(frontendClient);
    }

    private static void createBackendClient(Keycloak keycloak) {
        ClientRepresentation backendClient = new ClientRepresentation();
        backendClient.setClientId("backend-" + REALMNAME);
        backendClient.setName("backend");
        backendClient.setRedirectUris(List.of("/*"));
        backendClient.setWebOrigins(List.of("/*"));
        backendClient.setPublicClient(false);
        backendClient.setDirectAccessGrantsEnabled(true);
        keycloak.realm(REALMNAME).clients().create(backendClient);
    }

    private static void configUserProfileAttributes(Keycloak keycloak) {
        UPConfig upConfig =
                keycloak.realm(REALMNAME).users().userProfile().getConfiguration();
        List<UPAttribute> upAttributes = upConfig.getAttributes();

        UPAttribute role = new UPAttribute();
        role.setName("role");
        role.setDisplayName("Role");
        role.setGroup("user-metadata");
        upAttributes.add(role);

        UPAttribute function = new UPAttribute();
        function.setName("function");
        function.setDisplayName("Fonction");
        function.setGroup("user-metadata");
        upAttributes.add(function);

        UPAttribute statut = new UPAttribute();
        statut.setName("statut");
        statut.setDisplayName("Statut");
        statut.setGroup("user-metadata");
        statut.addValidation("options", Map.of("options", List.of("active", "inactive")));
        upAttributes.add(statut);
        upConfig.setAttributes(upAttributes);

        keycloak.realm(REALMNAME).users().userProfile().update(upConfig);
    }

    private static void createRoles(Keycloak keycloak) {
        final String[] permissions =
                {"import", "modify", "duplicate", "download", "copy_link", "share",
                        "delete"};

        final String[] folders = {"web", "mobile", "sm", "plv", "campagnes"};

        for (String folder : folders) {
            for (String permission : permissions) {
                RoleRepresentation r = new RoleRepresentation();
                r.setName(folder + "_" + permission);
                keycloak.realm(REALMNAME).roles().create(r);
            }
        }

        final String[] roles =
                {"user_add", "user_modify", "user_delete", "role_add", "role_modify",
                        "role_delete",};

        for (String role : roles) {
            RoleRepresentation r = new RoleRepresentation();
            r.setName(role);
            keycloak.realm(REALMNAME).roles().create(r);
        }
    }
}

