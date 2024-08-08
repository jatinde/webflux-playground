package com.github.jatinde.webflux_payground.entity;

import java.util.Objects;

import org.springframework.data.annotation.Id;

import com.github.jatinde.webflux_payground.dto.CustomerDto;

public record Customer(@Id Integer id, String name, String email) {
    
    public Customer(String name, String email) {
        this(null, name, email);
    }

    public static Customer from(CustomerDto customerDto) {
        return switch (customerDto) {
            case CustomerDto(Integer id,String name, String email) when Objects.isNull(id) -> new Customer(name, email);
            case CustomerDto(Integer id, String name, String email) -> new Customer(id, name, email);
            default -> throw new RuntimeException("Not valid customer. Bad Request.");
        };
    }

    public static CustomerDto to(Customer customer) {
        return switch (customer) {
            case Customer(var id, var name, var email) when Objects.isNull(id) -> new CustomerDto( name, email);
            case Customer(var id, var name, var email) -> new CustomerDto(id, name, email);
            default -> throw new RuntimeException("Not valid customer.");
        };
    }
}
