package com.example.DepositAPI.security;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import com.example.DepositAPI.DTO.DepositRequest;
import com.example.DepositAPI.DTO.EncryptedRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HybridTestClient {

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        AESUtil aesUtil = new AESUtil();
        RSAUtil rsaUtil = new RSAUtil();
        DigitalSignatureUtil signatureUtil = new DigitalSignatureUtil();
        KeyLoader keyLoader = new KeyLoader();

        // ==========================================================
        // 🔑 USE ONLY DEPOSIT KEYS (TEST MODE)
        // ==========================================================

        PrivateKey depositPrivateKey = keyLoader.loadPrivateKey();
        PublicKey depositPublicKey = keyLoader.loadDepositPublicKey();

        // Loans public key still used for encryption if needed
        PublicKey loansPublicKey = keyLoader.loadLoansPublicKey();

        System.out.println("=== STEP 1: Create Request ===");

        DepositRequest request = new DepositRequest();
        request.setName("Anna");
        request.setLastName("dfsdfdf");
        request.setEmail("anna@gmail.com");

        String requestJson = mapper.writeValueAsString(request);
        System.out.println("Original JSON: " + requestJson);

        // ==========================================================
        // 2️⃣ ENCRYPT + SIGN (TEST MODE)
        // ==========================================================

        SecretKey aesKey = aesUtil.generateAESKey();
        AESUtil.AESResult aesResult = aesUtil.encrypt(requestJson, aesKey);

        // Encrypt AES key using Deposit Public Key
        String encryptedAESKey =
                rsaUtil.encryptAESKey(aesKey.getEncoded(), depositPublicKey);

        // 🔏 SIGN USING DEPOSIT PRIVATE KEY (TEST MODE)
        String signature =
                signatureUtil.sign(aesResult.getCipherText(), depositPrivateKey);

        EncryptedRequest encryptedRequest = new EncryptedRequest();
        encryptedRequest.setEncryptedKey(encryptedAESKey);
        encryptedRequest.setCipherText(aesResult.getCipherText());
        encryptedRequest.setSignature(signature);

        String finalEncryptedJson =
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(encryptedRequest);

        System.out.println("\n📌 COPY THIS INTO POSTMAN BODY:\n");
        System.out.println(finalEncryptedJson);

        System.out.println("\n🔥 Request Ready (TEST MODE)");

        // ==========================================================
        // 3️⃣ VERIFY + DECRYPT (TEST MODE)
        // ==========================================================

        // 🔍 VERIFY USING DEPOSIT PUBLIC KEY
        boolean validSignature = signatureUtil.verify(
                encryptedRequest.getCipherText(),
                encryptedRequest.getSignature(),
                depositPublicKey
        );

        if (!validSignature) {
            throw new SecurityException("Invalid Digital Signature!");
        }

        System.out.println("Signature Verified ✅");

        byte[] decryptedAES =
                rsaUtil.decryptAESKey(encryptedRequest.getEncryptedKey(), depositPrivateKey);

        String decryptedJson =
                aesUtil.decrypt(encryptedRequest.getCipherText(), decryptedAES);

        System.out.println("Decrypted JSON: " + decryptedJson);

        System.out.println("\n🔥 TEST FLOW SUCCESSFUL 🔐");
    }
}