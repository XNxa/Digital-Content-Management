package com.dcm.backend.api;

import com.dcm.backend.dto.UserDTO;
import com.dcm.backend.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import ma.gov.mes.framework.keycloak.KeycloakProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    Keycloak keycloak;

    @Autowired
    KeycloakProperties keycloakProperties;

    @GetMapping("/count")
    public int countUsers() {
        return keycloak.realm(keycloakProperties.getRealm()).users().count();
    }

    @GetMapping("/list")
    public Collection<UserDTO> listUsers(
            @RequestParam(value = "firstResult", defaultValue = "0", required = false) int firstResult,
            @RequestParam(value = "maxResults", defaultValue = "10", required = false) int maxResults,
            @RequestBody UserDTO filter) {

        // Prepare query
        StringBuilder queryBuilder = new StringBuilder();
        if (filter.getEmail() != null) {
            queryBuilder.append("email:").append(filter.getEmail()).append(' ');
        }
        if (filter.getFirstname() != null) {
            queryBuilder.append("firstName:").append(filter.getFirstname()).append(' ');
        }
        if (filter.getLastname() != null) {
            queryBuilder.append("lastName:").append(filter.getLastname()).append(' ');
        }
        if (filter.getFunction() != null) {
            queryBuilder.append("function:").append(filter.getFunction()).append(' ');
        }
        if (filter.getRole() != null) {
            queryBuilder.append("role:").append(filter.getRole()).append(' ');
        }
        if (filter.getStatut() != null) {
            queryBuilder.append("statut:").append(filter.getStatut()).append(' ');
        }
        if (!queryBuilder.isEmpty())
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);

        return keycloak.realm(keycloakProperties.getRealm())
                .users()
                .searchByAttributes(
                        firstResult,
                        maxResults,
                        true,
                        false,
                        queryBuilder.toString()
                )
                .stream()
                .map(userRepresentation -> UserDTO.builder()
                        .email(userRepresentation.getEmail())
                        .firstname(userRepresentation.getFirstName())
                        .lastname(userRepresentation.getLastName())
                        .function(
                                userRepresentation.getAttributes().get("function").get(0))
                        .role(userRepresentation.getAttributes().get("role").get(0))
                        .statut(userRepresentation.getAttributes().get("statut").get(0))
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping("/create")
    public void createUser(@RequestBody @Valid UserDTO user) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstname());
        userRepresentation.setLastName(user.getLastname());
        userRepresentation.setEnabled(true);
        Map<String, List<String>> attributes =
                Map.of("function", List.of(user.getFunction()), "role",
                        List.of(user.getRole()), "statut", List.of(user.getStatut()));
        userRepresentation.setAttributes(attributes);

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(user.getPassword());
        credentials.setTemporary(false);
        userRepresentation.setCredentials(List.of(credentials));

        keycloak.realm(keycloakProperties.getRealm()).users().create(userRepresentation);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam("email") String email) throws
            UserNotFoundException {
        List<UserRepresentation> users =
                keycloak.realm(keycloakProperties.getRealm()).users().search(email);

        if (users.isEmpty()) {
            throw new UserNotFoundException("User not found : " + email);
        }

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .delete(users.get(0).getId());
    }

    @PutMapping("/update")
    public void updateUser(@RequestBody @Valid UserDTO user) throws
            UserNotFoundException {
        List<UserRepresentation> users =
                keycloak.realm(keycloakProperties.getRealm())
                        .users()
                        .search(user.getEmail());

        if (users.isEmpty()) {
            throw new UserNotFoundException("User not found : " + user.getEmail());
        }

        UserRepresentation userRepresentation = users.get(0);
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstname());
        userRepresentation.setLastName(user.getLastname());
        userRepresentation.setEnabled(true);
        Map<String, List<String>> attributes =
                Map.of("function", List.of(user.getFunction()), "role",
                        List.of(user.getRole()), "statut", List.of(user.getStatut()));
        userRepresentation.setAttributes(attributes);

        if (user.getPassword() != null) {
            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(user.getPassword());
            credentials.setTemporary(false);
            userRepresentation.setCredentials(List.of(credentials));
        }

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userRepresentation.getId())
                .update(userRepresentation);
    }

}
