package com.example.DepositAPI.security;


import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DepositAPI.DTO.EncryptedRequest;
import com.example.DepositAPI.DTO.EncryptedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HybridEncryptionService {

    @Autowired
    private KeyLoader keyLoader;

    @Autowired
    private RSAUtil rsaUtil;

    @Autowired
    private AESUtil aesUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 🔓 Decrypt Incoming Request
    public <T> T decryptRequest(EncryptedRequest request, Class<T> clazz) throws Exception {

        // 1️⃣ Load Deposit Private Key
        PrivateKey privateKey = keyLoader.loadPrivateKey();

        // 2️⃣ Decrypt AES key
        byte[] aesKeyBytes = rsaUtil.decryptAESKey(request.getEncryptedKey(), privateKey);

        // 3️⃣ Decrypt Payload
        String decryptedJson = aesUtil.decrypt(
                request.getCipherText(),
                request.getIv(),
                aesKeyBytes
        );

        // 4️⃣ Convert JSON → DTO
        return objectMapper.readValue(decryptedJson, clazz);
    }

    // 🔐 Encrypt Outgoing Response
    public EncryptedResponse encryptResponse(Object responseObject) throws Exception {

        // 1️⃣ Convert response to JSON
        String json = objectMapper.writeValueAsString(responseObject);

        // 2️⃣ Generate AES key
        SecretKey aesKey = aesUtil.generateAESKey();

        // 3️⃣ Encrypt JSON
        AESUtil.AESResult aesResult = aesUtil.encrypt(json, aesKey);

        // 4️⃣ Load Loans Public Key
        PublicKey loansPublicKey = keyLoader.loadLoansPublicKey();

        // 5️⃣ Encrypt AES key with Loans public key
        String encryptedAESKey = rsaUtil.encryptAESKey(aesKey.getEncoded(), loansPublicKey);

        return new EncryptedResponse(
                encryptedAESKey,
                aesResult.getIv(),
                aesResult.getCipherText()
        );
    }
}