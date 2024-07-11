package com.dcm.backend.utils.mappers;

import com.dcm.backend.dto.PermissionDTO;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    default PermissionDTO toPermissionDTO(RoleRepresentation roleRepresentation) {
        PermissionDTO permission = new PermissionDTO();
        permission.setPermission(roleRepresentation.getName());

        if (roleRepresentation.getAttributes() == null) {
            return permission;
        }
        List<String> attr = roleRepresentation.getAttributes().get("DisplayName");
        if (attr == null) {
            return null;
        }
        if (attr.size() == 4) {
            permission.setFolder(attr.get(0));
            permission.setSubfolder(attr.get(1));
            permission.setName(attr.get(2));
            permission.setPosition(Integer.parseInt(attr.get(3)));
        } else if (attr.size() == 3) {
            permission.setFolder(attr.get(0));
            permission.setSubfolder("");
            permission.setName(attr.get(1));
            permission.setPosition(Integer.parseInt(attr.get(2)));
        }
        return permission;
    }

}

