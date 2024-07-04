package com.dcm.backend.api;

import com.dcm.backend.dto.PermissionDTO;
import com.dcm.backend.dto.RoleDTO;
import com.dcm.backend.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin
@RestController
@RequestMapping("/public/api/role")
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

    @DeleteMapping("/delete")
    public void deleteRole(@RequestParam("name") String name) {
        roleService.deleteRole(name);
    }

    @PutMapping("/update")
    public void updateRole(@RequestBody @Valid RoleDTO role) {
        roleService.updateRole(role);
    }

    @PostMapping("/create")
    public void createRole(@RequestBody @Valid RoleDTO role) {
        roleService.createRole(role);
    }

    @GetMapping("/permissions")
    public Collection<PermissionDTO> listPermissions() {
        return roleService.getPermissions();
    }
}
