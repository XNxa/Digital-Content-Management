package com.dcm.backend.services.impl;

import com.dcm.backend.dto.UserDTO;
import com.dcm.backend.exceptions.UserNotFoundException;
import com.dcm.backend.services.UserService;
import com.dcm.backend.utils.mappers.UserMapper;
import ma.gov.mes.framework.keycloak.KeycloakProperties;
import org.jetbrains.annotations.Nullable;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private KeycloakProperties keycloakProperties;

    @Autowired
    private UserMapper userMapper;

    @Override
    public int count() {
        return keycloak.realm(keycloakProperties.getRealm()).users().count();
    }

    @Override
    public Collection<UserDTO> list(int firstResult, int maxResults, UserDTO filter) {
        String query = buildQuery(filter);

        return keycloak.realm(keycloakProperties.getRealm())
                .users()
                .searchByAttributes(firstResult, maxResults, true, false, query)
                .stream()
                .map(userMapper::toUserDTO)
                .toList();
    }

    @Override
    public void create(UserDTO user) {
        UserRepresentation userRepresentation = userMapper.toUserRepresentation(user);

        setUserPassword(userRepresentation, user.getPassword(), true);

        keycloak.realm(keycloakProperties.getRealm()).users().create(userRepresentation);
    }

    @Override
    public void delete(String id) {
        keycloak.realm(keycloakProperties.getRealm()).users().delete(id);
    }

    @Override
    public void update(UserDTO user) throws UserNotFoundException {
        UserRepresentation userRepresentation = findUserByEmail(user.getEmail());

        if (userRepresentation == null)
            throw new UserNotFoundException("User not found: " + user.getEmail());

        updateUserRepresentation(user, userRepresentation);

        List<GroupRepresentation> oldGroups = getUserGroups(userRepresentation.getId());
        List<GroupRepresentation> newGroups = getGroupByName(user.getRole());
        updateUserGroups(userRepresentation, oldGroups, newGroups);

        if (user.getPassword() != null)
            setUserPassword(userRepresentation, user.getPassword(), false);

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userRepresentation.getId())
                .update(userRepresentation);
    }

    @Override
    public UserDTO getUser(String id) {
        UserRepresentation userRepresentation =
                keycloak.realm(keycloakProperties.getRealm())
                        .users()
                        .get(id)
                        .toRepresentation(true);

        return userMapper.toUserDTO(userRepresentation);
    }

    private String buildQuery(UserDTO filter) {
        StringBuilder queryBuilder = new StringBuilder();
        if (filter.getEmail() != null)
            queryBuilder.append("email:").append(filter.getEmail()).append(' ');
        if (filter.getFirstname() != null)
            queryBuilder.append("firstName:").append(filter.getFirstname()).append(' ');
        if (filter.getLastname() != null)
            queryBuilder.append("lastName:").append(filter.getLastname()).append(' ');
        if (filter.getFunction() != null)
            queryBuilder.append("function:").append(filter.getFunction()).append(' ');
        if (filter.getRole() != null)
            queryBuilder.append("role:").append(filter.getRole()).append(' ');
        if (filter.getStatut() != null)
            queryBuilder.append("statut:").append(filter.getStatut()).append(' ');
        if (!queryBuilder.isEmpty()) queryBuilder.deleteCharAt(queryBuilder.length() - 1);

        return queryBuilder.toString();
    }

    @Nullable
    private UserRepresentation findUserByEmail(String email) {
        return keycloak.realm(keycloakProperties.getRealm())
                .users()
                .search(email)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private List<GroupRepresentation> getUserGroups(String userId) {
        return keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userId)
                .groups();
    }

    private List<GroupRepresentation> getGroupByName(String roleName) {
        return keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .groups()
                .stream()
                .filter(group -> group.getName().equals(roleName))
                .toList();
    }

    private void updateUserRepresentation(UserDTO user, UserRepresentation userRepresentation) {
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstname());
        userRepresentation.setLastName(user.getLastname());
        userRepresentation.setEnabled(true);
        userRepresentation.setAttributes(Map.of(
                "function", List.of(user.getFunction()),
                "role", List.of(user.getRole()),
                "statut", List.of(user.getStatut())
        ));
    }

    private void updateUserGroups(UserRepresentation userRepresentation, List<GroupRepresentation> oldGroups, List<GroupRepresentation> newGroups) {
        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userRepresentation.getId())
                .leaveGroup(oldGroups.get(0).getId());

        keycloak.realm(keycloakProperties.getRealm())
                .users()
                .get(userRepresentation.getId())
                .joinGroup(newGroups.get(0).getId());
    }

    private void setUserPassword(UserRepresentation userRepresentation,
                                 String newPassword, Boolean temporary) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(newPassword);
        credentials.setTemporary(temporary);
        userRepresentation.setCredentials(List.of(credentials));
    }
}
