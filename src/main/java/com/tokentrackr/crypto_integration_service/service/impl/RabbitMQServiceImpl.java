package com.tokentrackr.crypto_integration_service.service.impl;

import com.tokentrackr.crypto_integration_service.model.CryptoData;
import com.tokentrackr.crypto_integration_service.service.interfaces.MessageQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RabbitMQServiceImpl implements MessageQueueService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQServiceImpl.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name:crypto-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:crypto.data}")
    private String routingKey;

    @Value("${rabbitmq.batch.routing.key:crypto.data.batch}")
    private String batchRoutingKey;

    @Autowired
    public RabbitMQServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishCryptoDataBatch(List<CryptoData> cryptoDataList) {
        try {
            logger.info("Publishing batch of {} crypto data entries", cryptoDataList.size());
            rabbitTemplate.convertAndSend(exchangeName, batchRoutingKey, cryptoDataList);
            logger.debug("Successfully published batch of {} crypto data entries", cryptoDataList.size());
        } catch (Exception e) {
            logger.error("Failed to publish crypto data batch: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish batch message to RabbitMQ", e);
        }
    }
}
