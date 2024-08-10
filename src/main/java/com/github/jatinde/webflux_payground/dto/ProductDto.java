package com.github.jatinde.webflux_payground.dto;

public record ProductDto(Integer id, String description, Integer price) {
    public ProductDto(String description, Integer price) {
        this(null, description, price);
    }
}
