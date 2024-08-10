package com.github.jatinde.webflux_payground;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.jatinde.webflux_payground.dto.ProductDto;
import com.github.jatinde.webflux_payground.dto.UploadResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProductClient {
    
    public Mono<UploadResponse> uploadProducts(Flux<ProductDto> flux) {
        var webClient = WebClient.builder()
                                .baseUrl("http://localhost:8080")
                                .build();
        
        return webClient.post()
                    .uri("/products/upload")
                    .header("auth-token", "secret456")
                    .contentType(MediaType.APPLICATION_NDJSON)
                    .body(flux, ProductDto.class)
                    .retrieve()
                    .bodyToMono(UploadResponse.class);
    }

    public Flux<ProductDto> downloadProducts() {
        var webClient = WebClient.builder()
                                .baseUrl("http://localhost:8080")
                                .build();
        
        return webClient.get()
                    .uri("/products/download")
                    .header("auth-token", "secret456")
                    .accept(MediaType.APPLICATION_NDJSON)
                    .retrieve()
                    .bodyToFlux(ProductDto.class);
    }
}
