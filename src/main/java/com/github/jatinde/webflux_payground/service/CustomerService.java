package com.github.jatinde.webflux_payground.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.github.jatinde.webflux_payground.dto.CustomerDto;
import com.github.jatinde.webflux_payground.entity.Customer;
import com.github.jatinde.webflux_payground.repositories.CustomerRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    public Flux<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
                        .map(Customer::to);
    }

    public Flux<CustomerDto> getAllCustomers(Integer page, Integer size) {
        return customerRepository.findBy(PageRequest.of(page, size))
                        .map(Customer::to);
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return customerRepository.findById(id)
                    .map(Customer::to);
    }

    public Mono<CustomerDto> save(Mono<CustomerDto> customer) {
        return customer.<Customer>map(Customer::from)
                .flatMap(customerRepository::save)
                .map(Customer::to);
    }

    public Mono<CustomerDto> update(Integer id, Mono<CustomerDto> customer) {
        return  customerRepository.findById(id)
                                .flatMap(enity -> customer.map(c -> new Customer(id, c.name(), c.email())))
                                .flatMap(this.customerRepository::save)
                                .map(Customer::to);
    }

    public Mono<Boolean> delete(Integer id) {
        return  customerRepository.deletCustomerById(id);
    }
    
}
