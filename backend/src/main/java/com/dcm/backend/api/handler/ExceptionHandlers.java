package com.dcm.backend.api.handler;

import com.dcm.backend.exceptions.FileNotFoundException;
import com.dcm.backend.exceptions.NoThumbnailException;
import com.dcm.backend.utils.ErrorMessages;
import io.minio.errors.MinioException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler({IOException.class, MinioException.class,
            NoSuchAlgorithmException.class, InvalidKeyException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseError handleIOException(IOException ex) {
        return new ResponseError(ErrorMessages.ERROR_MESSAGE_INTERNAL_SERVER_ERROR_CODE,
                ex.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseError handleFileNotFoundException(FileNotFoundException ex) {
        return new ResponseError(ErrorMessages.ERROR_MESSAGE_FILE_NOT_FOUND_CODE,
                ex.getMessage());
    }

    @ExceptionHandler(NoThumbnailException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseError handleNoThumbnailException(NoThumbnailException ex) {
        return new ResponseError(ErrorMessages.ERROR_MESSAGE_NO_THUMBNAIL_CODE,
                ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseError handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseError(ErrorMessages.ERROR_MESSAGE_BAD_REQUEST_CODE,
                ex.getMessage());
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
