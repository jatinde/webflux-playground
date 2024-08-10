package com.github.jatinde.webflux_payground;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.jatinde.webflux_payground.dto.ProductDto;
import com.github.javafaker.Faker;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@AutoConfigureWebTestClient
@SpringBootTest(properties= {"repo=repositories"})
public class ProductControllerTest {
    
    private static final Logger log = LoggerFactory.getLogger(ProductControllerTest.class);

    private final ProductClient productClient = new ProductClient();

    private final Faker faker = Faker.instance();

    //@Test
    public void upload() {
        var flux = Flux.range(1, 1_000_000).<ProductDto>map(i -> {
            var c = faker.commerce();
            return new ProductDto(c.productName(), Double.valueOf(c.price()).intValue());
        });

        productClient.uploadProducts(flux)
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    //@Test
    public void download() {
        productClient.downloadProducts()
                .map(ProductDto::toString)
                .as(f -> FileWriter.create(f, Path.of("products.txt")))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
