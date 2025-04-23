package com.tokentrackr.crypto_integration_service.service.interfaces;

import com.tokentrackr.crypto_integration_service.model.CryptoData;

import java.util.List;

public interface GetAllCryptoService {
    List<CryptoData> getAllCrypto(int page, int size);
}
