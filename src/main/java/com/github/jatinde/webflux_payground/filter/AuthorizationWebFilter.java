package com.github.jatinde.webflux_payground.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.github.jatinde.webflux_payground.web.CustomerController;

import reactor.core.publisher.Mono;

@Order(2)
@Service
public class AuthorizationWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private FilterErrorHandler errorHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var category = (Category)exchange.getAttributeOrDefault("category", Category.STANDARD);
        var isGet = HttpMethod.GET.name().equals(exchange.getRequest().getMethod().name());
        log.info("isGet: {}", isGet);
        return switch (category) {
            case PRIME -> chain.filter(exchange);
            case STANDARD  -> isGet ? 
                chain.filter(exchange) : 
                errorHandler.sendProblemDetail(exchange, HttpStatus.FORBIDDEN, "Forbidden: Invalid token.");

        };


    }

    private Mono<Void> standard(ServerWebExchange exchange, WebFilterChain chain) {
        var isGet = HttpMethod.GET.name().equals(exchange.getRequest().getMethod().name());
        log.info("isGet: {}", isGet);
        if(isGet) {
            return chain.filter(exchange);
        }
        return errorHandler.sendProblemDetail(exchange, HttpStatus.FORBIDDEN, "Forbidden: Invalid token.");
    }
    
}
