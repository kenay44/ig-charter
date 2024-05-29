package com.task.rewardprogram.controllers;

import com.task.rewardprogram.exceptions.DtoValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@ControllerAdvice
public class ValidationExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> notValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getAllErrors().stream()
                .map((DefaultMessageSourceResolvable::getDefaultMessage))
                .toList();

        return ResponseEntity.badRequest()
                .body(List.of(errors));
    }

    @ExceptionHandler({DtoValidationException.class})
    public ResponseEntity<?> notValid(DtoValidationException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatus())
                .body(List.of(ex.getMessage()));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> argumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        logger.warn("Incorrect argument type {}", ex.getName(), ex);
        return ResponseEntity.badRequest()
                .body(List.of("Incorrect value of " + ex.getName()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<?> messageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        logger.warn("Unreadable requests", ex);
        return ResponseEntity.badRequest()
                .body(List.of("Request not readable " + ex.getMessage()));
    }

    @ExceptionHandler({Exception.class})
    public  ResponseEntity<?> unexpectedException(Exception ex, HttpServletRequest request) {
        logger.error("Internal server error", ex);
        return ResponseEntity.internalServerError()
                .body(List.of("Internal server error. " + ex.getMessage()));
    }
}
