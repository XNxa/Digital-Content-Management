package com.dcm.backend.services.impl;

import com.dcm.backend.dto.FileHeaderDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service("util")
public class AuthorizationServiceImpl {

    private final static String[] imageTypes =
            {"image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp",
                    "image/tiff",};

    private final static String[] videoTypes = {"video/%",};

    private final static String[] pictoTypes = {"image/svg+xml"};

    public String buildPermission(FileHeaderDTO metadata, String basePermission) {

        String[] parts = metadata.getFilename().split("/");
        if (parts.length < 2) {
            return "false";
        }
        String folder = parts[0];

        assert List.of("web", "mobile", "sm", "plv", "campagnes").contains(folder);

        String type;
        // Metadata type is in imageTypes
        if (Arrays.stream(imageTypes).anyMatch(metadata.getType()::equals)) {
            type = "images";
        } else if (Arrays.stream(videoTypes).anyMatch(metadata.getType()::equals)) {
            type = "videos";
        } else if (Arrays.stream(pictoTypes).anyMatch(metadata.getType()::equals)) {
            type = "pictos";
        } else {
            type = "docs";
        }

        return String.join("_", folder, type, basePermission);
    }

}
