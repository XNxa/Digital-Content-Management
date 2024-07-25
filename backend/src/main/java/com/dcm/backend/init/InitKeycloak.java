package com.dcm.backend.init;

import jakarta.ws.rs.ProcessingException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.userprofile.config.UPAttribute;
import org.keycloak.representations.userprofile.config.UPAttributePermissions;
import org.keycloak.representations.userprofile.config.UPConfig;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * This is a script to initialize Keycloak with the necessary configuration for the application.
 * It creates a new realm, two clients (frontend and backend), and adds some custom attributes to the user profile.
 * The script will remove the realm if it already exists.
 */
public class InitKeycloak {

    public static final String URL = "http://localhost:4200";
    public static final String URLstar = URL + "/*";
    private static final String REALMNAME = "dcm";

    private static final String ADMIN_GROUP = "Admin";
    private static final String ADMIN_USER = "admin@admin";
    private static final String ADMIN_PASSWORD = "admin";

    private static final String[] PERMISSIONS =
            {"consult", "import", "modify", "duplicate", "download", "copy_link", "share",
                    "delete"};
    private static final String[] DISPLAY_PERMISSIONS =
            {"Consulter les fichiers", "Importer un fichier",
                    "Modifier les informations d'un fichier", "Dupliquer un fichier",
                    "Télécharger un fichier", "Copier le lien d'un fichier",
                    "Partager par e-mail un fichier", "Supprimer un fichier"};
    private static final String[] FOLDER = {"web", "mobile", "sm", "plv", "campagnes"};
    private static final String[] DISPLAY_FOLDERS =
            {"Web", "Mobile", "SM", "P.L.V", "Campagnes"};
    private static final String[] SUBFOLDER = {"images", "videos", "pictos", "docs"};
    private static final String[] DISPLAY_SUBFOLDERS =
            {"Images", "Vidéos", "Pictos", "Documents"};
    private static final String[] OTHER_ROLES =
            {"user_consult", "user_add", "user_modify", "user_delete", "role_consult",
                    "role_add", "role_modify", "role_delete"};
    private static final String[] DISPLAY_OTHER_ROLES =
            {"Consulter les utilisateurs", "Ajouter un nouvel utilisateur",
                    "Modifier le profil de l'utilisateur", "Supprimer un utilisateur",
                    "Consulter les rôles", "Ajouter un nouveau rôle", "Modifier un rôle",
                    "Supprimer un rôle"};
    private static final String[] FOLDERS_OTHER_ROLES =
            {"Utilisateurs", "Utilisateurs", "Utilisateurs", "Utilisateurs", "Rôles",
                    "Rôles", "Rôles", "Rôles"};

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

            createAdminGroup(keycloak);
            System.out.println("Admin group created successfully");

            createAdminUser(keycloak);
            System.out.println("Admin user created successfully");

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
        realm.setLoginTheme("dcm");
        keycloak.realms().create(realm);
    }

    private static void createFrontendClient(Keycloak keycloak) {
        ClientRepresentation frontendClient = new ClientRepresentation();
        frontendClient.setClientId("frontend-" + REALMNAME);
        frontendClient.setName("frontend");
        frontendClient.setBaseUrl(URL);
        frontendClient.setRedirectUris(List.of(URLstar));
        frontendClient.setAttributes(Map.of("post.logout.redirect.uris", URLstar));
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

        UPAttributePermissions upAttributePermissions = new UPAttributePermissions();
        upAttributePermissions.setEdit(Set.of("admin"));
        upAttributePermissions.setView(Set.of("admin", "user"));

        UPAttribute role = new UPAttribute();
        role.setName("role");
        role.setDisplayName("Role");
        role.setGroup("user-metadata");
        role.setPermissions(upAttributePermissions);
        upAttributes.add(role);

        UPAttribute function = new UPAttribute();
        function.setName("function");
        function.setDisplayName("Fonction");
        function.setGroup("user-metadata");
        function.setPermissions(upAttributePermissions);
        upAttributes.add(function);

        UPAttribute statut = new UPAttribute();
        statut.setName("statut");
        statut.setDisplayName("Statut");
        statut.setGroup("user-metadata");
        statut.setPermissions(upAttributePermissions);
        statut.addValidation("options", Map.of("options", List.of("active", "inactive")));
        upAttributes.add(statut);
        upConfig.setAttributes(upAttributes);

        keycloak.realm(REALMNAME).users().userProfile().update(upConfig);
    }

    private static void createRoles(Keycloak keycloak) {

        int position = 0;

        for (int i = 0; i < FOLDER.length; i++) {
            String folder = FOLDER[i];
            for (int j = 0; j < SUBFOLDER.length; j++) {
                String subfolder = SUBFOLDER[j];
                for (int k = 0; k < PERMISSIONS.length; k++) {
                    String permission = PERMISSIONS[k];
                    RoleRepresentation r = new RoleRepresentation();
                    r.setName(folder + "_" + subfolder + "_" + permission);
                    r.setAttributes(Map.of("DisplayName",
                            List.of(DISPLAY_FOLDERS[i], DISPLAY_SUBFOLDERS[j],
                                    DISPLAY_PERMISSIONS[k], String.valueOf(position++))));
                    keycloak.realm(REALMNAME).roles().create(r);
                }
            }
        }

        for (int i = 0; i < OTHER_ROLES.length; i++) {
            String role = OTHER_ROLES[i];
            RoleRepresentation r = new RoleRepresentation();
            r.setName(role);
            r.setAttributes(Map.of("DisplayName",
                    List.of(FOLDERS_OTHER_ROLES[i], DISPLAY_OTHER_ROLES[i],
                            String.valueOf(position++))));
            keycloak.realm(REALMNAME).roles().create(r);
        }
    }

    private static void createAdminGroup(Keycloak keycloak) {
        GroupRepresentation adminGroup = new GroupRepresentation();
        adminGroup.setName(ADMIN_GROUP);
        adminGroup.setAttributes(Map.of("description", List.of("Role d'administrateur"),
                "state", List.of("true")));
        keycloak.realm(REALMNAME).groups().add(adminGroup);
        adminGroup = keycloak.realm(REALMNAME).groups().groups(ADMIN_GROUP, 0, 1,
                false).get(0);

        List<RoleRepresentation> roles = keycloak.realm(REALMNAME).roles().list();

        keycloak.realm(REALMNAME)
                .groups()
                .group(adminGroup.getId())
                .roles()
                .realmLevel()
                .add(roles);
    }

    private static void createAdminUser(Keycloak keycloak) {
        UserRepresentation adminUser = new UserRepresentation();
        adminUser.setUsername(ADMIN_USER);
        adminUser.setEmail(ADMIN_USER);
        adminUser.setFirstName("Admin");
        adminUser.setLastName("Admin");
        adminUser.setEnabled(true);
        adminUser.setAttributes(Map.of("role", List.of(ADMIN_GROUP), "function",
                List.of("admin"), "statut", List.of("active")));
        adminUser.setGroups(List.of(ADMIN_GROUP));
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(ADMIN_PASSWORD);
        adminUser.setCredentials(List.of(credential));
        keycloak.realm(REALMNAME).users().create(adminUser);
    }
}

