package com.github.jatinde.webflux_payground.webclient;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class AbstractWebClient {

    @Bean
    public WebClient createWebClient() {
        return createWebClient(v -> {
            var poolSize = 1;
            var provider = ConnectionProvider.builder("jay")
                                            .lifo().maxConnections(poolSize).build();
            
            var httpClient = HttpClient.create(provider).protocol(HttpProtocol.H2C)
                                .compress(true)
                                .keepAlive(true);
            v.clientConnector(new ReactorClientHttpConnector(httpClient));
        });
    }
    
    public WebClient createWebClient(Consumer<WebClient.Builder> webClientConsumer) {
        var webClient = WebClient.builder().baseUrl("http://192.168.1.6:7070/");
        webClientConsumer.accept(webClient);
        return webClient.build();
    }
}
