package com.dcm.backend.services.impl;

import com.dcm.backend.dto.UserDTO;
import com.dcm.backend.exceptions.UserNotFoundException;
import com.dcm.backend.services.UserService;
import ma.gov.mes.framework.keycloak.KeycloakProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    Keycloak keycloak;

    @Autowired
    KeycloakProperties keycloakProperties;

    @Override
    public int count() {
        return keycloak.realm(keycloakProperties.getRealm()).users().count();
    }

    @Override
    public Collection<UserDTO> list(int firstResult, int maxResults, UserDTO filter) {
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
        if (!queryBuilder.isEmpty()) queryBuilder.deleteCharAt(queryBuilder.length() - 1);

        return keycloak.realm(keycloakProperties.getRealm())
                .users()
                .searchByAttributes(firstResult, maxResults, true, false,
                        queryBuilder.toString())
                .stream()
                .map(userRepresentation -> UserDTO.builder()
                        .id(userRepresentation.getId())
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

    @Override
    public void create(UserDTO user) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstname());
        userRepresentation.setLastName(user.getLastname());
        userRepresentation.setEnabled(true);
        Map<String, List<String>> attributes =
                Map.of("function", List.of(user.getFunction()), "role",
                        List.of(user.getRole()), "statut", List.of(user.getStatut()));
        userRepresentation.setAttributes(attributes);
        userRepresentation.setGroups(List.of(user.getRole()));

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(user.getPassword());
        credentials.setTemporary(false);
        userRepresentation.setCredentials(List.of(credentials));

        keycloak.realm(keycloakProperties.getRealm()).users().create(userRepresentation);
    }

    @Override
    public void delete(String id) {
        keycloak.realm(keycloakProperties.getRealm()).users().delete(id);
    }

    @Override
    public void update(UserDTO user) throws UserNotFoundException {
        List<UserRepresentation> users = keycloak.realm(keycloakProperties.getRealm())
                .users()
                .search(user.getEmail());

        if (users.isEmpty()) {
            throw new UserNotFoundException("User not found : " + user.getEmail());
        }

        List<GroupRepresentation> oldGroups =
                keycloak.realm(keycloakProperties.getRealm())
                        .users()
                        .get(users.get(0).getId())
                        .groups();

        List<GroupRepresentation> newGroups =
                keycloak.realm(keycloakProperties.getRealm())
                        .groups()
                        .groups()
                        .stream()
                        .filter(groupRepresentation -> groupRepresentation.getName()
                                .equals(user.getRole()))
                        .toList();

        UserRepresentation userRepresentation = users.get(0);
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstname());
        userRepresentation.setLastName(user.getLastname());
        userRepresentation.setEnabled(true);
        Map<String, List<String>> attributes =
                Map.of("function", List.of(user.getFunction()), "role",
                        List.of(user.getRole()), "statut", List.of(user.getStatut()));
        userRepresentation.setAttributes(attributes);

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userRepresentation.getId())
                .leaveGroup(oldGroups.get(0).getId());

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userRepresentation.getId())
                .joinGroup(newGroups.get(0).getId());

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

    @Override
    public UserDTO getUser(String id) {
        UserRepresentation u = keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(id)
                .toRepresentation(true);

        String group = keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(id)
                .groups()
                .stream()
                .findFirst()
                .get()
                .getName();

        return UserDTO.builder()
                .id(u.getId())
                .email(u.getEmail())
                .firstname(u.getFirstName())
                .lastname(u.getLastName())
                .function(u.getAttributes().get("function").get(0))
                .role(group)
                .statut(u.getAttributes().get("statut").get(0))
                .build();
    }
}

