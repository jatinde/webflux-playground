package com.github.jatinde.webflux_payground.entity;

import java.util.Objects;

import org.springframework.data.annotation.Id;

import com.github.jatinde.webflux_payground.dto.ProductDto;

public record Product(@Id Integer id, String description, Integer price) {
    
    public Product(String description, Integer price) {
        this(null, description, price);
    }

     public static ProductDto to(Product product) {
        return switch (product) {
            case Product(var id, var description, var price) when Objects.isNull(id) -> new ProductDto( description, price);
            case Product(var id, var description, var price) -> new ProductDto(id, description, price);
            default -> throw new RuntimeException("Not valid Product.");
        };
    }

    public static Product from(ProductDto productDto) {
        return switch (productDto) {
            case ProductDto(Integer id, String description, var price) when Objects.isNull(id) -> new Product(description, price);
            case ProductDto(Integer id, String description, var price) -> new Product(id, description, price);
            default -> throw new RuntimeException("Not valid Product. Bad Request.");
        };
    }
}
