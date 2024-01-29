package com.example.shoes_shop.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class HandlerException {

    @Qualifier("messageSource")
    private final MessageSource msgSource;

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<Object> handlerInternalServerException(InternalServerException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handlerNotFoundException(NotFoundException ex) {
        //Log error
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handlerBadRequestException(BadRequestException ex) {
        //Log error
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handlerException(Exception ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> processValidateError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        String message = "";
        for (FieldError error : fieldErrors) {
            String temp = processFieldError(error);
            message += temp + " ; ";
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private String processFieldError(FieldError error) {
        String msg = "";
        if (error != null) {
            Locale currentLocale = LocaleContextHolder.getLocale();
            try {
                msg = msgSource.getMessage(error.getDefaultMessage(), null, currentLocale);
            } catch (Exception e) {
                msg = error.getDefaultMessage();
            }
        }
        return msg;
    }
}
