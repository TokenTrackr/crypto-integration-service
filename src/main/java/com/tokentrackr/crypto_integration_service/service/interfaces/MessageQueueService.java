package com.tokentrackr.crypto_integration_service.service.interfaces;

import com.tokentrackr.crypto_integration_service.model.CryptoData;

import java.util.List;

public interface MessageQueueService {

    void publishCryptoDataBatch(List<CryptoData> cryptoDataList);
}
