package com.example.DepositAPI.security;



import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class AESUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // 🔑 Generate AES-256 key
    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    // 🔐 Encrypt Data using AES-GCM
    public AESResult encrypt(String plainText, SecretKey secretKey) throws Exception {

        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        return new AESResult(
                Base64.getEncoder().encodeToString(iv),
                Base64.getEncoder().encodeToString(cipherText)
        );
    }

    // 🔓 Decrypt Data using AES-GCM
    public String decrypt(String base64CipherText, String base64Iv, byte[] aesKeyBytes) throws Exception {

        byte[] iv = Base64.getDecoder().decode(base64Iv);
        byte[] cipherText = Base64.getDecoder().decode(base64CipherText);

        SecretKey secretKey = new SecretKeySpec(aesKeyBytes, "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] decrypted = cipher.doFinal(cipherText);

        return new String(decrypted);
    }

    // Helper class
    public static class AESResult {
        private final String iv;
        private final String cipherText;

        public AESResult(String iv, String cipherText) {
            this.iv = iv;
            this.cipherText = cipherText;
        }

        public String getIv() {
            return iv;
        }

        public String getCipherText() {
            return cipherText;
        }
    }
}