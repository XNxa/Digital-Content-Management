package com.dcm.backend.validation.validators;

import com.dcm.backend.enumeration.Folders;
import com.dcm.backend.enumeration.Subfolders;
import com.dcm.backend.validation.constraints.Folder;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FolderValidator implements ConstraintValidator<Folder, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String[] parts = value.split("/");
        if (parts.length != 2) {
            return false;
        }

        try {
            Folders.valueOf(parts[0]);
            Subfolders.valueOf(parts[1]);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
