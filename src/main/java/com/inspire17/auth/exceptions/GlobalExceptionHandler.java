package com.inspire17.auth.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<?> handleServerRequestFailed(ServerException ex) {
        logger.error("ServerRequestFailed Exception: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getCode())
                .body(new ErrorResponse(ex.getMessage(), ex.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        logger.error("Unexpected Exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Server Error", 500));
    }
}

@Getter
@AllArgsConstructor
class ErrorResponse {
    private String message;
    private int status;
}
