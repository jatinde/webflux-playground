package com.github.jatinde.webflux_payground.exceptions;

public class ProductNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Product with [id = %d] Not found";

    public ProductNotFoundException(Integer id) {
        super(MESSAGE.formatted(id));
    }
    
}
