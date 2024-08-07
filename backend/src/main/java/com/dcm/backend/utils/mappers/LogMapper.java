package com.dcm.backend.utils.mappers;

import com.dcm.backend.dto.LogDTO;
import com.dcm.backend.entities.Log;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogMapper {

    @Mapping(target = "date", source = "date")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "action", source = "action")
    @Mapping(target = "before", source = "before")
    @Mapping(target = "after", source = "after")
    LogDTO toDTO(Log log);

}
