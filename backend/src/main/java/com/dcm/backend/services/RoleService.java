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
    Long countRoles();

    /**
     * List roles
     * @param firstResult the index of first result
     * @param maxResults the number of wanted results
     * @param filter the filter
     * @return the roles
     */
    Collection<RoleDTO> getRoles(int firstResult, int maxResults, RoleDTO filter);

    /**
     * Delete a role
     * @param name the name of the role
     */
    void deleteRole(String name);

    /**
     * Update a role
     * @param roleDTO the role
     */
    void updateRole(RoleDTO roleDTO);

    /**
     * Create a role
     * @param roleDTO the role
     */
    void createRole(RoleDTO roleDTO);

    /**
     * Get all permissions
     * @return the permissions
     */
    Collection<PermissionDTO> getPermissions();

    /**
     * Get a role
     * @param id the id of the role
     * @return the role
     */
    RoleDTO getRole(String id);
}
