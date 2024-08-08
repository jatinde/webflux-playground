package com.github.jatinde.webflux_payground.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.jatinde.webflux_payground.dto.CustomerDto;
import com.github.jatinde.webflux_payground.exceptions.ApplicationExceptions;
import com.github.jatinde.webflux_payground.service.CustomerService;
import com.github.jatinde.webflux_payground.validator.RequestValidator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Value("${server.port}")
    private int port;

    @Value("${server.address}")
    private String host;

    @GetMapping
    public Flux<CustomerDto> getAllCustomers() {
        var allCustomers = customerService.getAllCustomers();
        return allCustomers;
    }

    @GetMapping(value= "/paginated")
    public Mono<List<CustomerDto>> getAllCustomersPaginated(@RequestParam(defaultValue="1") Integer page, @RequestParam(defaultValue="3") Integer size) {
        var allCustomers = customerService.getAllCustomers(page - 1, size);
        return allCustomers.collectList();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<CustomerDto>> getCustomerById(@PathVariable Integer id) {
        var customer = customerService.getCustomerById(id);
        
        return customer.map(ResponseEntity::ok)
                    .switchIfEmpty(ApplicationExceptions.customerNotFoundException(id));
    }

    @PostMapping
    public Mono<ResponseEntity<CustomerDto>> saveCustomer(@RequestBody Mono<CustomerDto> customer) {
        var validatedCustomer = customer.transform(RequestValidator.validate());
        var savedCustomer = validatedCustomer.as(customerService::save);
        return savedCustomer.map(
            c -> ResponseEntity.created(UriComponentsBuilder.newInstance()
            .scheme("http").host(host).port(port)
            .path("/customers/{id}").build(c.id().toString())).build());
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<CustomerDto>> updaeCustomer(@PathVariable Integer id, @RequestBody Mono<CustomerDto> customer) {
        var validatedCustomer = customer.transform(RequestValidator.validate());
        var updateCustomer = validatedCustomer.as(dto -> customerService.update(id, dto));
        return updateCustomer.map(ResponseEntity::ok)
                    .switchIfEmpty(ApplicationExceptions.customerNotFoundException(id));
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteCustomerById(@PathVariable Integer id) {
        return customerService.delete(id)
                    .filter(b -> b)
                    .map(b -> ResponseEntity.ok().<Void>build())
                    .switchIfEmpty(ApplicationExceptions.customerNotFoundException(id));
    }
    
}
