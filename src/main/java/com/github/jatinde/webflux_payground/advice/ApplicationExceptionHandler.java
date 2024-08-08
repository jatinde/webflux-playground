package com.github.jatinde.webflux_payground.advice;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import com.github.jatinde.webflux_payground.exceptions.CustomerNotFoundException;
import com.github.jatinde.webflux_payground.exceptions.InvalidInputException;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

      private static final Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);
    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleException(CustomerNotFoundException ex) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("http://example.com/problems/not-found"));
        problemDetail.setTitle("Customer Not Found.");
        return problemDetail;
    }

    @ExceptionHandler(InvalidInputException.class)
    public ProblemDetail handleException(InvalidInputException ex) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create("http://example.com/problems/invalid-input"));
        problemDetail.setTitle("Invalid Input.");
        return problemDetail;
    }
    
}
