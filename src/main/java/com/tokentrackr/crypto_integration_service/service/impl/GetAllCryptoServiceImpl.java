package com.tokentrackr.crypto_integration_service.service.impl;

import com.tokentrackr.crypto_integration_service.model.CryptoData;
import com.tokentrackr.crypto_integration_service.service.interfaces.GetAllCryptoService;
import com.tokentrackr.crypto_integration_service.util.ApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetAllCryptoServiceImpl implements GetAllCryptoService {

    private static final Logger logger = LoggerFactory.getLogger(GetAllCryptoServiceImpl.class);
    private static final String CRYPTO_API_URL = "/coins/markets";

    private final WebClient webClient;

    @Value("${coingecko.api.base-url:https://api.coingecko.com/api/v3}")
    private String baseUrl;

    @Autowired
    public GetAllCryptoServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CryptoData> getAllCrypto(int page, int size) {
        logger.info("Fetching all cryptocurrencies from {}", CRYPTO_API_URL);

        return ApiUtils.executeWithRetry(() -> {
            URI requestUri = UriComponentsBuilder.fromPath(CRYPTO_API_URL)
                    .queryParam("vs_currency", "usd")
                    .queryParam("order", "market_cap_desc")
                    .queryParam("per_page", size)
                    .queryParam("page", page)
                    .build(true)
                    .toUri();

            logger.info("Requesting URL: {}{}", baseUrl, requestUri);
            List<CryptoData> cryptoList = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(CRYPTO_API_URL)
                            .queryParam("vs_currency", "usd")
                            .queryParam("order", "market_cap_desc")
                            .queryParam("per_page", size)  // Maximum allowed by CoinGecko
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CryptoData>>() {})
                    .block();

            logger.info("Successfully fetched {} cryptocurrencies", cryptoList != null ? cryptoList.size() : 0);
            return cryptoList != null ? cryptoList : new ArrayList<>();
        }, "cryptocurrency market data");
    }
}
