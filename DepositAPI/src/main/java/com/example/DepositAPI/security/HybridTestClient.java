package com.example.DepositAPI.security;






import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import com.example.DepositAPI.DTO.DepositRequest;
import com.example.DepositAPI.DTO.EncryptedRequest;
import com.example.DepositAPI.DTO.EncryptedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HybridTestClient {

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        AESUtil aesUtil = new AESUtil();
        RSAUtil rsaUtil = new RSAUtil();
        KeyLoader keyLoader = new KeyLoader();

        // -----------------------------------------
        // 1️⃣ Generate TEST Loans Private Key
        // (Simulates real Loans system)
        // -----------------------------------------
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair loansKeyPair = generator.generateKeyPair();

        PublicKey loansPublicKey = loansKeyPair.getPublic();
        PrivateKey loansPrivateKey = loansKeyPair.getPrivate();

        System.out.println("=== STEP 1: Create Request ===");

        DepositRequest request = new DepositRequest();
        request.setName("Anna");
        request.setLastName("dfsdfdf");
        request.setEmail("anna@gmail.com");

        String requestJson = mapper.writeValueAsString(request);
        System.out.println("Original JSON: " + requestJson);

        // -----------------------------------------
        // 2️⃣ LOANS SIDE - Encrypt using Deposit Public Key
        // -----------------------------------------

        SecretKey aesKey = aesUtil.generateAESKey();
        AESUtil.AESResult aesResult = aesUtil.encrypt(requestJson, aesKey);

        PublicKey depositPublicKey = keyLoader.loadDepositPublicKey();

        String encryptedAESKey =
                rsaUtil.encryptAESKey(aesKey.getEncoded(), depositPublicKey);

        EncryptedRequest encryptedRequest = new EncryptedRequest();
        encryptedRequest.setEncryptedKey(encryptedAESKey);
        encryptedRequest.setIv(aesResult.getIv());
        encryptedRequest.setCipherText(aesResult.getCipherText());



// 5️⃣ Print Final JSON for Postman
        String finalEncryptedJson =
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(encryptedRequest);

        System.out.println("\nCOPY THIS INTO POSTMAN BODY:\n");
        System.out.println(finalEncryptedJson);

        System.out.println("\n🔥 Request Ready");



        System.out.println("Request Encrypted Successfully ✅");

        // -----------------------------------------
        // 3️⃣ DEPOSIT SIDE - Decrypt using Deposit Private Key
        // -----------------------------------------

        PrivateKey depositPrivateKey = keyLoader.loadPrivateKey();

        byte[] decryptedAES =
                rsaUtil.decryptAESKey(encryptedRequest.getEncryptedKey(), depositPrivateKey);

        String decryptedJson =
                aesUtil.decrypt(encryptedRequest.getCipherText(),
                        encryptedRequest.getIv(),
                        decryptedAES);

        System.out.println("Decrypted JSON at Deposit: " + decryptedJson);

        // -----------------------------------------
        // 4️⃣ DEPOSIT Encrypts Response using Loans Public Key
        // -----------------------------------------

        String response = "OK";

        SecretKey responseAES = aesUtil.generateAESKey();
        AESUtil.AESResult responseAESResult = aesUtil.encrypt(response, responseAES);

        String encryptedResponseKey =
                rsaUtil.encryptAESKey(responseAES.getEncoded(), loansPublicKey);

        EncryptedResponse encryptedResponse = new EncryptedResponse(
                encryptedResponseKey,
                responseAESResult.getIv(),
                responseAESResult.getCipherText()
        );

        System.out.println("Response Encrypted by Deposit ✅");

        // -----------------------------------------
        // 5️⃣ LOANS Decrypts Response using Loans Private Key
        // -----------------------------------------

        byte[] decryptedResponseAES =
                rsaUtil.decryptAESKey(encryptedResponse.getEncryptedKey(), loansPrivateKey);

        String finalResponse =
                aesUtil.decrypt(encryptedResponse.getCipherText(),
                        encryptedResponse.getIv(),
                        decryptedResponseAES);

        System.out.println("Final Response at Loans: " + finalResponse);

        System.out.println("\n🔥 FULL HYBRID FLOW SUCCESSFUL 🔐");
    }
}