package com.github.jatinde.webflux_payground.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.github.jatinde.webflux_payground.common.model.Product;

import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {
    Flux<Product> findAllByPriceBetween(int start, int end);
    Flux<Product> findBy(Pageable pageable);
}
