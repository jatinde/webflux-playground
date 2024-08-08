package com.github.jatinde.webflux_payground.exceptions;

import reactor.core.publisher.Mono;

public class ApplicationExceptions {
    
    public static <T> Mono<T> customerNotFoundException(Integer id) {
        return Mono.error(new CustomerNotFoundException(id));
    }

    public static <T> Mono<T> missingNameException() {
        return Mono.error(new InvalidInputException("Name field is required."));
    }

    public static <T> Mono<T> invalidEmailException() {
        return Mono.error(new InvalidInputException("Valid Email field is required."));
    }
}
