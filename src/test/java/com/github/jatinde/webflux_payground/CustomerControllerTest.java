package com.github.jatinde.webflux_payground;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.jatinde.webflux_payground.dto.CustomerDto;


@AutoConfigureWebTestClient
@SpringBootTest(properties= {"repo=repositories"})
public class CustomerControllerTest {
    
    private static final Logger log = LoggerFactory.getLogger(CustomerControllerTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testAllCustomers() {
        webTestClient.get()
            .uri("/customers")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(CustomerDto.class)
            .value(c -> log.info("{}", c))
            .hasSize(10);
    }

    @Test
    public void testPaginatedCustomers() {
        webTestClient.get()
            .uri("/customers/paginated?page=2&size=3")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .consumeWith(c -> {log.info("{}", new String(c.getResponseBody()));})
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[0].id").isEqualTo(4)
            .jsonPath("$[1].id").isEqualTo(5)
            .jsonPath("$[2].id").isEqualTo(6)
            .jsonPath("$[0].name").isEqualTo("emily")
            .jsonPath("$[1].name").isEqualTo("sophia")
            .jsonPath("$[2].name").isEqualTo("liam");
    }

    @Test
    public void testCustomerById() {
        webTestClient.get()
            .uri("/customers/2")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            //.expectBody(CustomerDto.class)
            .expectBody()
            .consumeWith(c -> {log.info("{}", new String(c.getResponseBody()));})
            .jsonPath("$.id").isEqualTo(2)
            .jsonPath("$.name").isEqualTo("mike")
            .jsonPath("$.email").isEqualTo("mike@gmail.com");
    }

    @Test
    public void testCreateAndDelete() {
        var dto =new CustomerDto("jat", "jat@kubehub.in");
        webTestClient.post()
            .uri("/customers")
            .bodyValue(dto)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectHeader().location("http://localhost:8080/customers/11")
            .expectBody().isEmpty();
            

        webTestClient.delete()
        .uri("/customers/11")
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody().isEmpty();

    }

    @Test
    public void testUpdate() {
        var dto =new CustomerDto("jat", "jat@kubehub.in");
        webTestClient.put()
            .uri("/customers/3")
            .bodyValue(dto)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            //.expectBody(CustomerDto.class)
            .expectBody()
            .consumeWith(c -> {log.info("{}", new String(c.getResponseBody()));})
            .jsonPath("$.id").isEqualTo(3)
            .jsonPath("$.name").isEqualTo("jat")
            .jsonPath("$.email").isEqualTo("jat@kubehub.in");

    }

    @Test
    public void customerNotFound() {
        webTestClient.get()
        .uri("/customers/11")
        .exchange()
        .expectStatus().is4xxClientError()
        .expectBody().isEmpty();

        webTestClient.delete()
        .uri("/customers/11")
        .exchange()
        .expectStatus().is4xxClientError()
        .expectBody().isEmpty();

        var dto =new CustomerDto("jat", "jat@kubehub.in");
        webTestClient.put()
            .uri("/customers/11")
            .bodyValue(dto)
            .exchange()
            .expectStatus().is4xxClientError()
            .expectBody().isEmpty();
    }
}
