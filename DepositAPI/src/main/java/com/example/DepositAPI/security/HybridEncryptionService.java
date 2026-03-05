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

    @Autowired
    private DigitalSignatureUtil signatureUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==========================================================
    // 🔓 Decrypt Incoming Request (Verify → Decrypt)
    // ==========================================================
    public <T> T decryptRequest(EncryptedRequest request, Class<T> clazz) throws Exception {

        // 1️⃣ Load Loans Public Key (to verify signature)


//Uncomment below line and comment line below to that line in time of real testing.


        // PublicKey loansPublicKey = keyLoader.loadLoansPublicKey();
        PublicKey loansPublicKey=keyLoader.loadDepositPublicKey();

        // 2️⃣ Verify Digital Signature
        boolean isValidSignature = signatureUtil.verify(
                request.getCipherText(),
                request.getSignature(),
                loansPublicKey
        );

        if (!isValidSignature) {
            throw new SecurityException("Invalid Digital Signature");
        }

        // 3️⃣ Load Deposit Private Key (to decrypt AES key)
        PrivateKey depositPrivateKey = keyLoader.loadPrivateKey();

        // 4️⃣ Decrypt AES key using RSA
        byte[] aesKeyBytes = rsaUtil.decryptAESKey(
                request.getEncryptedKey(),
                depositPrivateKey
        );

        // 5️⃣ Decrypt Payload (IV is already combined in cipherText)
        String decryptedJson = aesUtil.decrypt(
                request.getCipherText(),
                aesKeyBytes
        );

        // 6️⃣ Convert JSON → DTO
        return objectMapper.readValue(decryptedJson, clazz);
    }

    // ==========================================================
    // 🔐 Encrypt Outgoing Response (Encrypt → Sign)
    // ==========================================================
    public EncryptedResponse encryptResponse(Object responseObject) throws Exception {

        // 1️⃣ Convert response to JSON
        String json = objectMapper.writeValueAsString(responseObject);

        // 2️⃣ Generate AES key
        SecretKey aesKey = aesUtil.generateAESKey();

        // 3️⃣ Encrypt JSON (IV + Cipher combined internally)
        AESUtil.AESResult aesResult = aesUtil.encrypt(json, aesKey);

        // 4️⃣ Load Loans Public Key (to encrypt AES key)
        PublicKey loansPublicKey = keyLoader.loadLoansPublicKey();

        String encryptedAESKey = rsaUtil.encryptAESKey(
                aesKey.getEncoded(),
                loansPublicKey
        );

        // 5️⃣ Load Deposit Private Key (to sign response)
        PrivateKey depositPrivateKey = keyLoader.loadPrivateKey();

        String signature = signatureUtil.sign(
                aesResult.getCipherText(),
                depositPrivateKey
        );

        // 6️⃣ Return Final Encrypted Response
        return new EncryptedResponse(
                encryptedAESKey,
                aesResult.getCipherText(),
                signature
        );
    }
}