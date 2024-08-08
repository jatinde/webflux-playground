package com.github.jatinde.webflux_payground.validator;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import com.github.jatinde.webflux_payground.dto.CustomerDto;
import com.github.jatinde.webflux_payground.exceptions.ApplicationExceptions;

import reactor.core.publisher.Mono;

public class RequestValidator {
    
    private static final Predicate<CustomerDto> hasName = (dto) -> Objects.nonNull(dto.name());
    private static final Predicate<CustomerDto> validEmail = dto -> Objects.nonNull(dto.email()) && dto.email().contains("@") && dto.email().contains(".");
    
    public static UnaryOperator<Mono<CustomerDto>> validate() {
        return  mono -> mono.filter(hasName)
                    .switchIfEmpty(ApplicationExceptions.missingNameException())
                    .filter(validEmail)
                    .switchIfEmpty(ApplicationExceptions.invalidEmailException());
    }
}
