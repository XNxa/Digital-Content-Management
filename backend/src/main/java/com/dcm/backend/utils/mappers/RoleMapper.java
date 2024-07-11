package com.dcm.backend.utils.mappers;

import com.dcm.backend.dto.RoleDTO;
import org.keycloak.representations.idm.GroupRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", imports = {Map.class, List.class, Boolean.class,
        GroupRepresentation.class})
public interface RoleMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", expression = "java(groupRepresentation.getAttributes() != null ? groupRepresentation.getAttributes().get(\"description\").get(0) : null)")
    @Mapping(target = "state", expression = "java(groupRepresentation.getAttributes() != null ? Boolean.parseBoolean(groupRepresentation.getAttributes().get(\"state\").get(0)) : false)")
    @Mapping(target = "permissions", source = "realmRoles")
    RoleDTO toRoleDTO(GroupRepresentation groupRepresentation);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "attributes", expression = "java(Map.of(\"description\", List.of" +
            "(roleDTO.getDescription()), \"state\", List.of(String.valueOf(roleDTO.isState()))))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "subGroupCount", ignore = true)
    @Mapping(target = "realmRoles", ignore = true)
    @Mapping(target = "clientRoles", ignore = true)
    @Mapping(target = "subGroups", ignore = true)
    @Mapping(target = "access", ignore = true)
    GroupRepresentation toGroupRepresentation(RoleDTO roleDTO);

}
