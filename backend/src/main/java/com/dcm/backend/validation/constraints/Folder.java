package com.dcm.backend.validation.constraints;

import com.dcm.backend.validation.validators.FolderValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FolderValidator.class)
public @interface Folder {

    String message() default "Invalid folder";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
