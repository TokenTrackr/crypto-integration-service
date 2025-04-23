package com.tokentrackr.crypto_integration_service.scheduler;


import com.tokentrackr.crypto_integration_service.model.CryptoData;
import com.tokentrackr.crypto_integration_service.service.interfaces.GetAllCryptoService;
import com.tokentrackr.crypto_integration_service.service.interfaces.MessageQueueService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class CryptoDataScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CryptoDataScheduler.class);

    private final GetAllCryptoService getAllCryptoService;
    private final MessageQueueService messageQueueService;
    private final boolean schedulerEnabled;

    public CryptoDataScheduler(GetAllCryptoService getAllCryptoService,
                               MessageQueueService messageQueueService,
                               @Value("${crypto.scheduler.enabled:true}") boolean schedulerEnabled) {
        this.getAllCryptoService = getAllCryptoService;
        this.messageQueueService = messageQueueService;
        this.schedulerEnabled = schedulerEnabled;
    }

    @PostConstruct
    public void runOnceOnStartup() {
        logger.info("Running crypto scheduler once on application startup");
        runCryptoJob();
    }


    @Scheduled(fixedDelayString = "${crypto.scheduler.interval:600000}")
    public void fetchAndPublishCryptoData() {
        if (!schedulerEnabled) {
            logger.debug("Crypto data scheduler is disabled");
            return;
        }

        logger.info("Starting scheduled cryptocurrency data fetch");
        try {
            List<CryptoData> cryptoDataList = getAllCryptoService.getAllCrypto(1, 250);

            if (cryptoDataList != null && !cryptoDataList.isEmpty()) {
                logger.info("Publishing {} cryptocurrency entries to message queue", cryptoDataList.size());
                messageQueueService.publishCryptoDataBatch(cryptoDataList);
                logger.info("Completed publishing cryptocurrency data");
            } else {
                logger.warn("No cryptocurrency data retrieved, nothing to publish");
            }
        } catch (Exception e) {
            logger.error("Error in scheduled cryptocurrency data fetch: {}", e.getMessage(), e);
        }
    }
    private void runCryptoJob() {
        int page = 1;
        int size = 250;

        if (!schedulerEnabled) {
            logger.debug("Crypto data scheduler is disabled");
            return;
        }

        logger.info("Starting cryptocurrency data fetch");
        try {
            List<CryptoData> cryptoDataList = getAllCryptoService.getAllCrypto(page, size);

            if (cryptoDataList != null && !cryptoDataList.isEmpty()) {
                logger.info("Publishing {} cryptocurrency entries to message queue", cryptoDataList.size());
                messageQueueService.publishCryptoDataBatch(cryptoDataList);
                logger.info("Completed publishing cryptocurrency data");
            } else {
                logger.warn("No cryptocurrency data retrieved, nothing to publish");
            }
        } catch (Exception e) {
            logger.error("Error fetching cryptocurrency data: {}", e.getMessage(), e);
        }
    }
}