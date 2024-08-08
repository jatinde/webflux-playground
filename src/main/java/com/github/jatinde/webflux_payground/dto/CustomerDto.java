package com.github.jatinde.webflux_payground.dto;

public record CustomerDto(Integer id, String name, String email) {
    public CustomerDto(String name, String email) {
        this(null, name, email);
    }
}
