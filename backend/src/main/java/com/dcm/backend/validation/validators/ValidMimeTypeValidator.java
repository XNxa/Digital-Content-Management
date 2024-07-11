package com.dcm.backend.validation.validators;

import com.dcm.backend.validation.constraints.ValidMimeType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

/**
 * Validator for the ValidMimeType constraint
 */
public class ValidMimeTypeValidator implements ConstraintValidator<ValidMimeType,
        String> {

    @Override
    public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
        if (value != null) {
            try {
                MediaType.parseMediaType(value);
            } catch (InvalidMediaTypeException e) {
                return false;
            }
        }
        return true;
    }

}
