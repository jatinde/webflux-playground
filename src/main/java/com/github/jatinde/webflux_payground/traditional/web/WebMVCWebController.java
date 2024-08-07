package com.github.jatinde.webflux_payground.traditional.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import com.github.jatinde.webflux_payground.common.model.Product;

@RestController
@RequestMapping("traditional")
public class WebMVCWebController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebMVCWebController.class);

    private final RestClient restClient = RestClient.builder().baseUrl("http://jat.local:7070").build();

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        var productsList =restClient.get()
                .uri("/demo01/products")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Product>>() {});
        
        logger.info("Products list received from server: {}", productsList);
        return productsList;
    }
}
