package com.dcm.backend.utils.mappers;

import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.entities.elastic.FileHeaderElastic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {java.util.List.class,
        com.dcm.backend.entities.Keyword.class})
public interface FileHeaderMapper {

    @Mapping(target = "folder", source = "folder")
    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "thumbnailName", source = "thumbnailName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "keywords", expression = "java(fileHeader.getKeywords().stream()" +
            ".map(Keyword::getName).toList())")
    FileHeaderDTO toDto(FileHeader fileHeader);

    @Mapping(target = "folder", source = "folder")
    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "thumbnailName", source = "thumbnailName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "keywords", source = "keywords")
    FileHeaderDTO toDto(FileHeaderElastic fileHeaderElastic);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "folder", source = "folder")
    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "keywords", ignore = true)
    FileHeader toMinimalEntity(FileHeaderDTO fileHeaderDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "folder", source = "folder")
    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "thumbnailName", source = "thumbnailName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "keywords", source = "keywords")
    FileHeader copy(FileHeader fileHeader);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "folder", source = "folder")
    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "thumbnailName", source = "thumbnailName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "keywords", expression = "java(fileHeader.getKeywords().stream()" +
            ".map(Keyword::getName).toList())")
    FileHeaderElastic toElastic(FileHeader fileHeader);
}
