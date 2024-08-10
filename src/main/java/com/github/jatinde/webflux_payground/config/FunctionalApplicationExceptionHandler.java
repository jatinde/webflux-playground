package com.github.jatinde.webflux_payground.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import com.github.jatinde.webflux_payground.exceptions.CustomerNotFoundException;
import com.github.jatinde.webflux_payground.exceptions.InvalidInputException;
import com.github.jatinde.webflux_payground.exceptions.ProductNotFoundException;

import reactor.core.publisher.Mono;

@Service
public class FunctionalApplicationExceptionHandler extends ResponseEntityExceptionHandler {

      private static final Logger log = LoggerFactory.getLogger(FunctionalApplicationExceptionHandler.class);

    public Mono<ServerResponse> handleException(CustomerNotFoundException ex, ServerRequest request) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        var problemDetail = ProblemDetail.forStatusAndDetail(notFound, ex.getMessage());
        problemDetail.setType(URI.create("http://example.com/problems/not-found"));
        problemDetail.setInstance(URI.create(request.path()));
        problemDetail.setTitle("Customer Not Found.");
        return ServerResponse.status(notFound).bodyValue(problemDetail);
    }

    public Mono<ServerResponse> handleException(ProductNotFoundException ex, ServerRequest request) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        var problemDetail = ProblemDetail.forStatusAndDetail(notFound, ex.getMessage());
        problemDetail.setType(URI.create("http://example.com/problems/not-found"));
        problemDetail.setInstance(URI.create(request.path()));
        problemDetail.setTitle("Product Not Found.");
        return ServerResponse.status(notFound).bodyValue(problemDetail);
    }


    public Mono<ServerResponse> handleException(InvalidInputException ex, ServerRequest request) {
        log.info("ex.message {}", ex.getMessage());
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        var problemDetail = ProblemDetail.forStatusAndDetail(badRequest, ex.getMessage());
        problemDetail.setType(URI.create("http://example.com/problems/invalid-input"));
        problemDetail.setTitle("Invalid Input.");
        problemDetail.setInstance(URI.create(request.path()));
        return ServerResponse.status(badRequest).bodyValue(problemDetail);
    }
    
}
