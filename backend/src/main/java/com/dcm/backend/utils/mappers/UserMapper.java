package com.dcm.backend.utils.mappers;

import com.dcm.backend.dto.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring",
        imports = {Map.class, List.class} , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstname")
    @Mapping(target = "lastName", source = "lastname")
    @Mapping(target = "enabled", expression = "java(true)")
    @Mapping(target = "attributes", expression = "java(Map.of(\"function\",List.of(user" +
            ".getFunction()),\"role\",List.of(user.getRole()),\"statut\",List.of(user" +
            ".getStatut())))")
    @Mapping(target = "credentials", ignore = true)
    UserRepresentation toUserRepresentation(UserDTO user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstname", source = "firstName")
    @Mapping(target = "lastname", source = "lastName")
    @Mapping(target = "function", expression = "java(userRepresentation.getAttributes()" +
            ".get" +
            "(\"function\").get(0))")
    @Mapping(target = "role", expression = "java(userRepresentation.getAttributes().get" +
            "(\"role\").get(0))")
    @Mapping(target = "statut", expression = "java(userRepresentation.getAttributes()" +
            ".get" +
            "(\"statut\").get(0))")
    UserDTO toUserDTO(UserRepresentation userRepresentation);

}