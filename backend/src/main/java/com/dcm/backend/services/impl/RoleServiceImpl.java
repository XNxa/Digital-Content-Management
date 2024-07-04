package com.dcm.backend.services.impl;

import com.dcm.backend.dto.PermissionDTO;
import com.dcm.backend.dto.RoleDTO;
import com.dcm.backend.services.RoleService;
import ma.gov.mes.framework.keycloak.KeycloakProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    Keycloak keycloak;

    @Autowired
    KeycloakProperties keycloakProperties;

    @Override
    public Long countRoles() {
        return keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .count()
                .get("count");
    }

    @Override
    public Collection<RoleDTO> getRoles(int firstResult, int maxResults, RoleDTO filter) {
        return keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .groups("", firstResult, maxResults, false)
                .stream()
                .map(groupRepresentation -> {
                    RoleDTO roleDTO = new RoleDTO();
                    roleDTO.setName(groupRepresentation.getName());
                    Map<String, List<String>> attr = groupRepresentation.getAttributes();
                    roleDTO.setDescription(attr.get("description").get(0));
                    roleDTO.setState(Boolean.parseBoolean(attr.get("state").get(0)));
                    roleDTO.setPermissions(groupRepresentation.getRealmRoles());
                    return roleDTO;
                })
                .toList();
    }

    @Override
    public void deleteRole(String name) {
        keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .groups()
                .stream()
                .filter(groupRepresentation -> groupRepresentation.getName().equals(name))
                .findFirst()
                .ifPresent(groupRepresentation -> keycloak.realm(
                                keycloakProperties.getRealm())
                        .groups()
                        .group(groupRepresentation.getId())
                        .remove());
    }

    @Override
    public void updateRole(RoleDTO roleDTO) {
        GroupRepresentation groupRepresentation =
                keycloak.realm(keycloakProperties.getRealm())
                        .groups()
                        .groups()
                        .stream()
                        .filter(group -> group.getName().equals(roleDTO.getName()))
                        .findFirst()
                        .orElseThrow(); // TODO: custom exception

        groupRepresentation.setAttributes(
                Map.of("description", List.of(roleDTO.getDescription()), "state",
                        List.of(String.valueOf(roleDTO.isState()))));

        List<RoleRepresentation> roles = keycloak.realm(keycloakProperties.getRealm())
                .roles()
                .list()
                .stream()
                .filter(roleRepresentation -> roleDTO.getPermissions()
                        .contains(roleRepresentation.getName()))
                .toList();

        List<RoleRepresentation> rolesToRemove =
                keycloak.realm(keycloakProperties.getRealm())
                        .groups()
                        .group(groupRepresentation.getId())
                        .roles()
                        .realmLevel()
                        .listAll();

        keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(groupRepresentation.getId())
                .update(groupRepresentation);

        keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(groupRepresentation.getId())
                .roles()
                .realmLevel()
                .remove(rolesToRemove);

        keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(groupRepresentation.getId())
                .roles()
                .realmLevel()
                .add(roles);
    }

    @Override
    public void createRole(RoleDTO roleDTO) {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(roleDTO.getName());
        groupRepresentation.setAttributes(
                Map.of("description", List.of(roleDTO.getDescription()), "state",
                        List.of(String.valueOf(roleDTO.isState()))));

        keycloak.realm(keycloakProperties.getRealm()).groups().add(groupRepresentation);

        GroupRepresentation g = keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .groups()
                .stream()
                .filter(group -> group.getName().equals(roleDTO.getName()))
                .findFirst()
                .get();

        List<RoleRepresentation> roles = keycloak.realm(keycloakProperties.getRealm())
                .roles()
                .list()
                .stream()
                .filter(roleRepresentation -> roleDTO.getPermissions()
                        .contains(roleRepresentation.getName()))
                .toList();

        keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(g.getId())
                .roles()
                .realmLevel()
                .add(roles);
    }

    @Override
    public Collection<PermissionDTO> getPermissions() {
        return keycloak.realm(keycloakProperties.getRealm())
                .roles()
                .list(false)
                .stream()
                .map(roleRepresentation -> {
                    PermissionDTO permissionDTO = new PermissionDTO();
                    permissionDTO.setPermission(roleRepresentation.getName());
                    if (roleRepresentation.getAttributes() == null) {
                        return permissionDTO;
                    }
                    List<String> attr =
                            roleRepresentation.getAttributes().get("DisplayName");
                    if (attr == null) {
                        // ignore roles without DisplayName
                        return null;
                    }
                    if (attr.size() == 3) {
                        permissionDTO.setFolder(attr.get(0));
                        permissionDTO.setSubfolder(attr.get(1));
                        permissionDTO.setName(attr.get(2));
                    } else {
                        assert attr.size() == 2;
                        permissionDTO.setFolder(attr.get(0));
                        permissionDTO.setSubfolder("");
                        permissionDTO.setName(attr.get(1));
                    }
                    return permissionDTO;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
