package com.github.jatinde.webflux_payground.web;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.jatinde.webflux_payground.dto.ProductDto;
import com.github.jatinde.webflux_payground.dto.UploadResponse;
import com.github.jatinde.webflux_payground.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;
    
    @PostMapping(value = "upload", consumes=MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<UploadResponse> upload(@RequestBody Flux<ProductDto> flux) {

        log.info("invoked");
        return productService.saveAll(flux)
            .then(productService.count())
            .map(c -> new UploadResponse(UUID.randomUUID(), c));
    }

    @GetMapping(value="download", produces=MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ProductDto> download() {
        return productService.allProducts();
    }

    @PostMapping
    public Mono<ProductDto> saveProduct(@RequestBody Mono<ProductDto> product) {
        return productService.saveProduct(product);
    }

    @GetMapping(value="stream", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductDto> productStream() {
        return productService.productStream();
    }

    @GetMapping(value="stream/{maxPrice}", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductDto> productStreamMaxPrice(@PathVariable Integer maxPrice) {
        return productService.productStream().filter(p -> p.price() <= maxPrice);
    }
}
