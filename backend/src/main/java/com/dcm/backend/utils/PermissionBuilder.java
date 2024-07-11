package com.dcm.backend.utils;

import com.dcm.backend.dto.FileHeaderDTO;
import com.dcm.backend.entities.FileHeader;
import com.dcm.backend.repositories.FileRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Service("util")
public class PermissionBuilder {

    @Autowired
    private FileRepository fr;

    private final static String[] imageTypes =
            {"image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp",
                    "image/tiff",};

    private final static String[] videoTypes = {"video/%",};

    private final static String[] pictoTypes = {"image/svg+xml"};

    @NotNull
    public String buildPermission(@NotNull FileHeaderDTO metadata, String basePermission) {

        String folder = getFolder(metadata.getFilename());
        if (folder == null) return "false";

        String type;
        // Metadata type is in imageTypes
        if (match(imageTypes, metadata.getType())) {
            type = "images";
        } else if (match(videoTypes, metadata.getType())) {
            type = "videos";
        } else if (match(pictoTypes, metadata.getType())) {
            type = "pictos";
        } else {
            type = "docs";
        }

        return String.join("_", folder, type, basePermission);
    }


    @NotNull
    public String buildPermission(@NotNull String filename, String basePermission) {
        String folder = getFolder(filename);
        if (folder == null) return "false";

        String type;
        Optional<FileHeader> headerOptional = fr.findByFilename(filename);
        if (headerOptional.isPresent()) {
            FileHeader fileHeader = headerOptional.get();
            if (match(imageTypes, fileHeader.getType())) {
                type = "images";
            } else if (match(videoTypes, fileHeader.getType())) {
                type = "videos";
            } else if (match(pictoTypes, fileHeader.getType())) {
                type = "pictos";
            } else {
                type = "docs";
            }
        } else {
            return "false";
        }

        return String.join("_", folder, type, basePermission);
    }

    private static @Nullable String getFolder(@NotNull String filename) {
        String[] parts = filename.split("/");
        if (parts.length < 2) {
            return null;
        }
        String folder = parts[0];

        assert List.of("web", "mobile", "sm", "plv", "campagnes").contains(folder);
        return folder;
    }

    private boolean match(@NotNull String[] types, @NotNull String type) {
        return Arrays.stream(types).anyMatch(t -> {
            if (t.contains("%")) {
                return type.matches(t.replace("%", ".*"));
            } else {
                return t.equals(type);
            }
        });
    }
}
