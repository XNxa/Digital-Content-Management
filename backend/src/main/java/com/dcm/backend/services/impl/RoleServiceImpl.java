package com.dcm.backend.services.impl;

import com.dcm.backend.dto.PermissionDTO;
import com.dcm.backend.dto.RoleDTO;
import com.dcm.backend.services.RoleService;
import com.dcm.backend.utils.mappers.PermissionMapper;
import com.dcm.backend.utils.mappers.RoleMapper;
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
    private Keycloak keycloak;

    @Autowired
    private KeycloakProperties keycloakProperties;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

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
                .map(roleMapper::toRoleDTO)
                .toList();
    }

    @Override
    public void deleteRole(String id) {
        keycloak.realm(keycloakProperties.getRealm()).groups().group(id).remove();
    }

    @Override
    public void updateRole(RoleDTO roleDTO) {
        GroupRepresentation groupRepresentation = getGroupRepresentation(roleDTO.getId());
        updateGroupRepresentation(groupRepresentation, roleDTO);
        updateGroupRoles(groupRepresentation, roleDTO);
        updateUserRoles(groupRepresentation, roleDTO.getName());
    }

    @Override
    public void createRole(RoleDTO roleDTO) {
        GroupRepresentation groupRepresentation =
                roleMapper.toGroupRepresentation(roleDTO);
        keycloak.realm(keycloakProperties.getRealm()).groups().add(groupRepresentation);

        GroupRepresentation group = getGroupByName(roleDTO.getName());
        List<RoleRepresentation> roles = getRoleRepresentations(roleDTO.getPermissions());

        keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(group.getId())
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
                .map(permissionMapper::toPermissionDTO)
                .filter(Objects::nonNull)
                .sorted(this::comparePermissions)
                .toList();
    }

    @Override
    public RoleDTO getRole(String id) {
        GroupRepresentation group = keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(id)
                .toRepresentation();

        return roleMapper.toRoleDTO(group);
    }

    @Override
    public Collection<String> getActiveRoles() {
        return keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .groups("", 0, Math.toIntExact(this.countRoles()), false)
                .stream()
                .filter(this::isGroupActive)
                .map(GroupRepresentation::getName)
                .toList();
    }

    private GroupRepresentation getGroupRepresentation(String id) {
        return keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(id)
                .toRepresentation();
    }

    private void updateGroupRepresentation(GroupRepresentation groupRepresentation, RoleDTO roleDTO) {
        groupRepresentation.setName(roleDTO.getName());
        groupRepresentation.setAttributes(
                Map.of("description", List.of(roleDTO.getDescription()), "state",
                        List.of(String.valueOf(roleDTO.isState()))));
        keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(groupRepresentation.getId())
                .update(groupRepresentation);
    }

    private void updateGroupRoles(GroupRepresentation groupRepresentation, RoleDTO roleDTO) {
        List<RoleRepresentation> roles = getRoleRepresentations(roleDTO.getPermissions());
        List<RoleRepresentation> rolesToRemove =
                getCurrentGroupRoles(groupRepresentation.getId());

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

    private void updateUserRoles(GroupRepresentation groupRepresentation, String roleName) {
        keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(groupRepresentation.getId())
                .members()
                .forEach(user -> {
                    user.getAttributes().put("role", List.of(roleName));
                    keycloak.realm(keycloakProperties.getRealm())
                            .users()
                            .get(user.getId())
                            .update(user);
                });
    }

    private GroupRepresentation getGroupByName(String name) {
        return keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .groups()
                .stream()
                .filter(group -> group.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private List<RoleRepresentation> getRoleRepresentations(Collection<String> permissions) {
        return keycloak.realm(keycloakProperties.getRealm())
                .roles()
                .list()
                .stream()
                .filter(role -> permissions.contains(role.getName()))
                .toList();
    }

    private List<RoleRepresentation> getCurrentGroupRoles(String groupId) {
        return keycloak.realm(keycloakProperties.getRealm())
                .groups()
                .group(groupId)
                .roles()
                .realmLevel()
                .listAll();
    }

    private int comparePermissions(PermissionDTO p1, PermissionDTO p2) {
        return Integer.compare(p1.getPosition(), p2.getPosition());
    }

    private boolean isGroupActive(GroupRepresentation group) {
        if (group.getAttributes() == null) {
            return false;
        }
        List<String> attr = group.getAttributes().get("state");
        return attr != null && Boolean.parseBoolean(attr.get(0));
    }
}
