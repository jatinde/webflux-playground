package com.github.jatinde.webflux_payground.filter;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

//@Order(1)
//@Service
public class AuthenticationWebFilter implements WebFilter {
    @Autowired
    private FilterErrorHandler errorHandler;

    private static final Map<String, Category> TOKEN_CATEGORY_MAP = Map.of(
        "secret123", Category.STANDARD,
        "secret456", Category.PRIME
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("auth-token");
        if(exchange.getRequest().getPath().toString().endsWith("stream")) {
            return chain.filter(exchange);
        }

        if(Objects.nonNull(token) && TOKEN_CATEGORY_MAP.containsKey(token)) {
            exchange.getAttributes().put("category", TOKEN_CATEGORY_MAP.get(token));
            return chain.filter(exchange);
        }
        //return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
        return  errorHandler.sendProblemDetail(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized: Invalid token.");
    }
    
}
