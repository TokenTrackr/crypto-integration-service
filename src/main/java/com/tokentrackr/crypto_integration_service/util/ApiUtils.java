package com.tokentrackr.crypto_integration_service.util;



import com.tokentrackr.crypto_integration_service.exception.ApiIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Supplier;
import java.util.concurrent.TimeoutException;

public class ApiUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private ApiUtils() {
        // Utility class, no instantiation
    }

    /**
     * Execute an API call with retry logic
     *
     * @param <T> The return type of the API call
     * @param apiCall The API call to execute
     * @param resourceName Name of the resource being fetched (for logging)
     * @return The result of the API call
     * @throws ApiIntegrationException if all retries fail
     */
    public static <T> T executeWithRetry(Supplier<T> apiCall, String resourceName) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < MAX_RETRIES) {
            try {
                if (attempt > 0) {
                    long delayMs = RETRY_DELAY_MS * (long) Math.pow(2, attempt - 1);
                    logger.info("Retry attempt {} for {}, waiting {}ms", attempt, resourceName, delayMs);
                    Thread.sleep(delayMs);
                }

                return apiCall.get();

            } catch (WebClientResponseException e) {
                lastException = e;

                // Don't retry for client errors (except 429 Too Many Requests)
                if (e.getStatusCode().is4xxClientError() && e.getStatusCode() != HttpStatus.TOO_MANY_REQUESTS) {
                    logger.error("Client error fetching {}: {} {}", resourceName, e.getStatusCode(), e.getStatusText());
                    break;
                }

                logger.warn("Error fetching {} (attempt {}): {} {}", resourceName, attempt + 1,
                        e.getStatusCode(), e.getStatusText());

            } catch (Exception e) {
                lastException = e;
                logger.warn("Error fetching {} (attempt {}): {}", resourceName, attempt + 1, e.getMessage());
            }

            attempt++;
        }

        String errorMsg = String.format("Failed to fetch %s after %d attempts", resourceName, MAX_RETRIES);
        logger.error(errorMsg, lastException);
        throw new ApiIntegrationException(errorMsg, lastException);
    }

    /**
     * Checks if an exception is due to rate limiting
     */
    public static boolean isRateLimitError(Exception e) {
        if (e instanceof WebClientResponseException wcre) {
            return wcre.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS;
        }
        return false;
    }

    /**
     * Checks if an exception is due to a timeout
     */
    public static boolean isTimeoutError(Exception e) {
        return e instanceof TimeoutException ||
                (e.getCause() != null && e.getCause() instanceof TimeoutException);
    }
}
