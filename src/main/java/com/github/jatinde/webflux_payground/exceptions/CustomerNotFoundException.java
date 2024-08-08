package com.github.jatinde.webflux_payground.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Customer with [id = %d] Not found";

    public CustomerNotFoundException(Integer id) {
        super(MESSAGE.formatted(id));
    }
    
}
