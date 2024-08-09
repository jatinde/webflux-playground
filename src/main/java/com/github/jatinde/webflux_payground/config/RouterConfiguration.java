package com.github.jatinde.webflux_payground.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.jatinde.webflux_payground.exceptions.CustomerNotFoundException;
import com.github.jatinde.webflux_payground.exceptions.InvalidInputException;

@Configuration
public class RouterConfiguration {

    @Autowired
    private CustomerRequestHandler handler;

    @Autowired
    private  FunctionalApplicationExceptionHandler exceptionHandler;

    @Bean
    public RouterFunction<ServerResponse> customerRoutes() {
        return RouterFunctions.route()
                    .GET("/fn/customers", handler::allCustomers)
                    .GET("/fn/customers/paginated", handler::paginatedCustomers)
                    .GET("/fn/customers/{id}", handler::getCustomerById)
                    .POST("/fn/customers", handler::saveCustomer)
                    .PUT("/fn/customers/{id}", handler::updateCustomer)
                    .DELETE("/fn/customers/{id}", handler::deleteCustomerById)
                    .onError(CustomerNotFoundException.class, exceptionHandler::handleException)
                    .onError(InvalidInputException.class, exceptionHandler::handleException)
                    .build();
    }
    
}
