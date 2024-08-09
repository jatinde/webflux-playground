package com.github.jatinde.webflux_payground.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.jatinde.webflux_payground.dto.CustomerDto;
import com.github.jatinde.webflux_payground.exceptions.ApplicationExceptions;
import com.github.jatinde.webflux_payground.service.CustomerService;
import com.github.jatinde.webflux_payground.validator.RequestValidator;

import reactor.core.publisher.Mono;

@Service
public class CustomerRequestHandler {

    @Autowired
    private CustomerService customerService;
    
    public Mono<ServerResponse> allCustomers(ServerRequest request) {
        return customerService.getAllCustomers()
        .as(flux -> ServerResponse.ok()
        .body(flux, CustomerDto.class));
    }

    public Mono<ServerResponse> paginatedCustomers(ServerRequest request) {
        var page = request.queryParam("page").map(Integer::parseInt).orElse(1);
        var size = request.queryParam("size").map(Integer::parseInt).orElse(10);
        return customerService.getAllCustomers(page, size)
        .as(flux -> ServerResponse.ok()
        .body(flux, CustomerDto.class));
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest request) {
        var id = Integer.parseInt(request.pathVariable("id"));
        return customerService.getCustomerById(id)
        .switchIfEmpty(ApplicationExceptions.customerNotFoundException(id))
        .as(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> saveCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerDto.class)
                .transform(RequestValidator.validate())
                .as(customerService::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request) {
        var id = Integer.parseInt(request.pathVariable("id"));
        return request.bodyToMono(CustomerDto.class)
                .transform(RequestValidator.validate())
                .as(c -> customerService.update(id, c))
                .switchIfEmpty(ApplicationExceptions.customerNotFoundException(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> deleteCustomerById(ServerRequest request) {
        var id = Integer.parseInt(request.pathVariable("id"));
        return customerService.delete(id)
        .filter(c -> c)
        .switchIfEmpty(ApplicationExceptions.customerNotFoundException(id))
        .then(ServerResponse.ok().build());
    }
}
