package com.tokentrackr.crypto_integration_service.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class WebClientTest {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Test
    public void testWebClient() {
        // Replace with the actual base URL you're testing
        String baseUrl = "https://api.coingecko.com/";

        // WebClient test directly calling the endpoint
        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();

        String endpoint = "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1";

        // Make the WebClient request to the CoinGecko API and print the result
        String response = webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(String.class)  // Convert the response body to a String
                .doOnTerminate(() -> System.out.println("Request complete"))
                .block();  // Synchronously wait for the result

        // Assert that the response is not null to verify it's working
        assertNotNull(response);
        System.out.println("Response: " + response);
    }
}

