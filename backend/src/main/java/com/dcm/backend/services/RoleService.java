package com.dcm.backend.services;

import com.dcm.backend.dto.PermissionDTO;
import com.dcm.backend.dto.RoleDTO;

import java.util.Collection;

public interface RoleService {

    /**
     * Count the number of roles
     *
     * @return the number of roles
     */
    Long count();

    /**
     * List roles
     *
     * @param firstResult the index of first result
     * @param maxResults  the number of wanted results
     * @param filter      the filter
     * @return the roles
     */
    Collection<RoleDTO> list(int firstResult, int maxResults, RoleDTO filter);

    /**
     * Delete a role
     *
     * @param id keycloak id of the role
     */
    void delete(String id);

    /**
     * Update a role
     *
     * @param roleDTO the role
     */
    void update(RoleDTO roleDTO);

    /**
     * Create a role
     *
     * @param roleDTO the role
     */
    void create(RoleDTO roleDTO);

    /**
     * Get all permissions
     *
     * @return the permissions
     */
    Collection<PermissionDTO> getPermissions();

    /**
     * Get a role
     *
     * @param id the id of the role
     * @return the role
     */
    RoleDTO getRole(String id);

    /**
     * Get the names of all active roles
     *
     * @return the active roles
     */
    Collection<String> getActiveRoles();

    /**
     * Validate a role name
     *
     * @param name the future name of the role
     * @return true if the name is valid, false otherwise
     */
    boolean validate(String name);

    /**
     * Check if a role is currently used by a user
     *
     * @param id the id of the role
     * @return true if the role is not used, false otherwise
     */
    boolean isDeactivatable(String id);
}
