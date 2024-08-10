package com.github.jatinde.webflux_payground.config;

import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.jatinde.webflux_payground.dto.ProductDto;
import com.github.jatinde.webflux_payground.exceptions.ApplicationExceptions;
import com.github.jatinde.webflux_payground.validator.RequestValidator;

import reactor.core.publisher.Mono;

@Service
public class ProductRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ProductRequestHandler.class); 

    @Autowired
    private WebClient webClient;

    public Mono<ServerResponse> productById(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        var pathParams = Map.of(
            "id", id
        );
       return webClient.get()
                .uri("demo02/lec01/product/{id}", pathParams)
                .retrieve()
                .bodyToMono(ProductDto.class)
                 .switchIfEmpty(ApplicationExceptions.productNotFoundException(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> allProductStream(ServerRequest request) {
        
       var res = webClient.get()
                .uri("demo02/lec02/product/stream")
                .retrieve()
                .bodyToFlux(ProductDto.class)
                //.switchIfEmpty(ApplicationExceptions.productNotFoundException(id))
                .as(flux -> ServerResponse.ok().body(flux, ProductDto.class));

        return res;
    }

    public Mono<ServerResponse> postProduct(ServerRequest request) {
 
         return request.bodyToMono(ProductDto.class)
                    .transform(RequestValidator.validateProduct())
                    .as(mono -> webClient.post()
                    .uri("demo02/lec03/product")
                    .body(mono, ProductDto.class)
                    .retrieve()
                    .bodyToMono(ProductDto.class))
                    .flatMap(ServerResponse.ok()::bodyValue);
     }

     public Mono<ServerResponse> postProductExchange(ServerRequest request) {
 
        return request.bodyToMono(ProductDto.class)
                   .transform(RequestValidator.validateProduct())
                   .as(mono -> webClient.post()
                   .uri("demo02/lec03/product")
                   .body(mono, ProductDto.class)
                   .exchangeToMono(this::decode))
                   .flatMap(ServerResponse.ok()::bodyValue);
    }

    private Mono<ProductDto> decode(ClientResponse response) {
        log.info("Status Code {}", response.statusCode());

        if(response.statusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) {
            return response.bodyToMono(ProblemDetail.class)
                            .doOnNext(pd -> {log.info("{}", pd);})
                            .then(Mono.empty());
        }

        return response.bodyToMono(ProductDto.class);
    }

     public Mono<ServerResponse> productByIdHeader(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        var pathParams = Map.of(
            "id", id
        );
       return webClient.get()
                .uri("demo02/lec04/product/{id}", pathParams)
                .header("caller-id", "abc")
                .retrieve()
                .bodyToMono(ProductDto.class)
                .doOnError(WebClientResponseException.class, ex -> {log.info("{}", ex.getResponseBodyAs(ProblemDetail.class));})
                 .switchIfEmpty(ApplicationExceptions.productNotFoundException(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> productByIdBasicAuth(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        var pathParams = Map.of(
            "id", id
        );
        var authenticateToken = Base64.getEncoder().encodeToString("java:secret".getBytes());
       return webClient.get()
                .uri("demo02/lec07/product/{id}", pathParams)
                .header("Authorization", "Basic "+authenticateToken)
                .retrieve()
                .bodyToMono(ProductDto.class)
                 .switchIfEmpty(ApplicationExceptions.productNotFoundException(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> productByIdBasicBearer(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        var pathParams = Map.of(
            "id", id
        );
        var authenticateToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
       return webClient.get()
                .uri("demo02/lec08/product/{id}", pathParams)
                .header("Authorization", "Bearer "+authenticateToken)
                .retrieve()
                .bodyToMono(ProductDto.class)
                 .switchIfEmpty(ApplicationExceptions.productNotFoundException(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }
    
}
