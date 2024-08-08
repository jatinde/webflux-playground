package com.github.jatinde.webflux_payground.repositories;


import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.github.jatinde.webflux_payground.entity.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer>{

    Flux<Customer> findByName(String name);
    Flux<Customer> findByEmailEndsWith(String emailSuffix);

    Flux<Customer> findBy(Pageable pageable);

    @Modifying
    @Query("DELETE from customer where id=:id")
    Mono<Boolean> deletCustomerById(Integer id);
    
}
