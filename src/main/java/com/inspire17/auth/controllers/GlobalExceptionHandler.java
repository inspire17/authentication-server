package com.inspire17.auth.controllers;

import com.inspire17.auth.exceptions.ServerRequestFailed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerRequestFailed.class)
    public ResponseEntity<?> handleServerRequestFailed(ServerRequestFailed ex) {
        return ResponseEntity
                .status(ex.getCode())
                .body(new ErrorResponse(ex.getMessage(), ex.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
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
