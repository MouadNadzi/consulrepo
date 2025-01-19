package com.example.car.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
/*
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8888/service-client")
                .build();
    }
}*/
@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://service-client")
                .build();
    }
}