package com.github.jatinde.webflux_payground.reactive.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.jatinde.webflux_payground.common.model.Product;

import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;


@RequestMapping("reactive")
@RestController
public class ReactiveWebController {
    private static final Logger logger = LoggerFactory.getLogger(ReactiveWebController.class);
    
    HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);

    private final WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("http://jat.local:7070").build();

    @GetMapping("/products")
    public Flux<Product> getAllProducts() {
        return webClient.get()
                .uri("/demo01/products")
                .retrieve()
                .bodyToFlux(Product.class)
                .onErrorComplete()
                .doOnNext(p -> {logger.info("Product received from server: {}", p);});
    }

    @GetMapping(value= "/products/stream", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> getAllProductsStream() {
        return webClient.get()
                .uri("/demo01/products")
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext(p -> {logger.info("Product received from server: {}", p);});
    }
    
}
