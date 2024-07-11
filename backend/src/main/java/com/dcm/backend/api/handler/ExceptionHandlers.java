package com.dcm.backend.api.handler;

import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.exceptions.NoThumbnailException;
import com.dcm.backend.utils.ErrorMessages;
import io.minio.errors.MinioException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlers {

    @NotNull
    @ExceptionHandler({IOException.class, MinioException.class,
            NoSuchAlgorithmException.class, InvalidKeyException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseError handleIOException(@NotNull IOException ex) {
        return new ResponseError(ErrorMessages.INTERNAL_SERVER_ERROR_CODE,
                ex.getMessage());
    }

    @NotNull
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseError handleFileNotFoundException(@NotNull FileNotFoundException ex) {
        return new ResponseError(ErrorMessages.FILE_NOT_FOUND_CODE,
                ex.getMessage());
    }

    @NotNull
    @ExceptionHandler(NoThumbnailException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseError handleNoThumbnailException(@NotNull NoThumbnailException ex) {
        return new ResponseError(ErrorMessages.NO_THUMBNAIL_CODE,
                ex.getMessage());
    }

    @NotNull
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseError handleIllegalArgumentException(@NotNull IllegalArgumentException ex) {
        return new ResponseError(ErrorMessages.BAD_REQUEST_CODE,
                ex.getMessage());
    }

    @NotNull
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseError handleConstraintViolationException(@NotNull ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        return new ResponseError(ErrorMessages.CONSTRAINT_VIOLATION_CODE,
                String.join(", ", errors));
    }

    @Data
    public static class ResponseError {
        private String message;
        private String code;

        public ResponseError(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
