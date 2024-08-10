package com.github.jatinde.webflux_payground.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.jatinde.webflux_payground.dto.ProductDto;
import com.github.jatinde.webflux_payground.entity.Product;
import com.github.jatinde.webflux_payground.repositories.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Sinks.Many<ProductDto> sink;

    public Flux<ProductDto> saveAll(Flux<ProductDto> products) {
        return products.map(Product::from).as(productRepository::saveAll).map(Product::to);
    }

    public Mono<Long> count() {
        return  productRepository.count();
    }

    public Flux<ProductDto> allProducts() {
        return productRepository.findAll().map(Product::to);
    }

    public Mono<ProductDto> saveProduct(Mono<ProductDto> product) {
        return product.map(Product::from)
            .flatMap(productRepository::save)
            .map(Product::to)
            .doOnNext(sink::tryEmitNext);
    }

    public Flux<ProductDto> productStream() {
        return sink.asFlux();
    }
    
}
