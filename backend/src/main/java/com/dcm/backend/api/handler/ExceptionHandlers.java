package com.dcm.backend.api.handler;

import com.dcm.backend.utils.ErrorMessages;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError handleIOException(IOException ex) {
        return new ResponseError(ErrorMessages.ERROR_MESSAGE_INTERNAL_SERVER_ERROR_CODE,
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
