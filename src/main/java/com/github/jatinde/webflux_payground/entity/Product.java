package com.github.jatinde.webflux_payground.entity;

import org.springframework.data.annotation.Id;

public record Product(@Id Integer id, String description, Integer price) {
    
    public Product(String description, Integer price) {
        this(null, description, price);
    }
}
