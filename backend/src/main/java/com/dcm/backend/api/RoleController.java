package com.dcm.backend.api;

import com.dcm.backend.dto.PermissionDTO;
import com.dcm.backend.dto.RoleDTO;
import com.dcm.backend.services.RoleService;
import com.dcm.backend.utils.Permissions;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/count")
    public long countRoles() {
        return roleService.countRoles();
    }

    @PostMapping("/roles")
    public Collection<RoleDTO> listRoles(@RequestParam(value = "firstResult", defaultValue = "0", required = false) int firstResult,
                                         @RequestParam(value = "maxResults", defaultValue = "5", required = false) int maxResults,
                                         @RequestBody RoleDTO filter) {
        return roleService.getRoles(firstResult, maxResults, filter);
    }

    @GetMapping("/actives")
    public Collection<String> listActiveRoles() {
        return roleService.getActiveRoles();
    }

    @GetMapping("role")
    public RoleDTO getRole(@RequestParam("id") String id) {
        return roleService.getRole(id);
    }

    @PreAuthorize("hasRole('" + Permissions.ROLE_DELETE + "')")
    @DeleteMapping("/delete")
    public void deleteRole(@RequestParam("id") String id) {
        roleService.deleteRole(id);
    }

    @PreAuthorize("hasRole('" + Permissions.ROLE_MODIFY + "')")
    @PutMapping("/update")
    public void updateRole(@RequestBody @Valid RoleDTO role) {
        roleService.updateRole(role);
    }

    @PreAuthorize("hasRole('" + Permissions.ROLE_ADD + "')")
    @PostMapping("/create")
    public void createRole(@RequestBody @Valid RoleDTO role) {
        roleService.createRole(role);
    }

    @GetMapping("/permissions")
    public Collection<PermissionDTO> listPermissions() {
        return roleService.getPermissions();
    }

    @GetMapping("/validate")
    public boolean validateRole(@RequestParam("name") String name) {
        return roleService.validateRole(name);
    }

    @GetMapping("deactivatable")
    public boolean isDeactivatable(@RequestParam("id") String id) {
        return roleService.isDeactivatable(id);
    }
}
