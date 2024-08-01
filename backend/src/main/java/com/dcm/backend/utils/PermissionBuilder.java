package com.dcm.backend.utils;

import com.dcm.backend.dto.FileFilterDTO;
import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.dto.FilenameDTO;
import com.dcm.backend.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("unused")
@Service("util")
public class PermissionBuilder {

    @Autowired
    private FileRepository fr;

    public String buildPermission(FileFilterDTO metadata, String basePermission) {
        return buildPermission(metadata.getFolder(), basePermission);
    }

    public String buildPermission(FileHeaderDTO metadata, String basePermission) {
        return buildPermission(metadata.getFolder(), basePermission);
    }

    public String buildPermission(FilenameDTO file, String basePermission) {
        return buildPermission(file.getFolder(), basePermission);
    }

    private String buildPermission(String folder, String permission) {
        String[] parts = folder.split("/");
        assert parts.length == 2;
        return String.join("_", parts[0], parts[1], permission);
    }

}
