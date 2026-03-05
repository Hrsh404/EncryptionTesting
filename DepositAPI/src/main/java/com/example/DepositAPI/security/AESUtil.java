package com.example.DepositAPI.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
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
    private static final int GCM_IV_LENGTH = 12;     // 96 bits (recommended)
    private static final int GCM_TAG_LENGTH = 128;   // 128-bit auth tag

    // ==========================================================
    // 🔑 Generate AES-256 Key
    // ==========================================================
    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    // ==========================================================
    // 🔐 Encrypt (IV + CipherText + Tag Combined)
    // ==========================================================
    public AESResult encrypt(String plainText, SecretKey secretKey) throws Exception {

        // 1️⃣ Generate IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // 2️⃣ Init cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        // 3️⃣ Encrypt
        byte[] cipherText = cipher.doFinal(
                plainText.getBytes(StandardCharsets.UTF_8)
        );

        // 4️⃣ Combine IV + CipherText
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        // 5️⃣ Base64 encode
        String base64Combined = Base64.getEncoder().encodeToString(combined);

        return new AESResult(base64Combined);
    }

    // ==========================================================
    // 🔓 Decrypt (Extract IV from Combined)
    // ==========================================================
    public String decrypt(String base64Combined, byte[] aesKeyBytes) throws Exception {

        // 1️⃣ Decode Base64
        byte[] combined = Base64.getDecoder().decode(base64Combined);

        // 2️⃣ Extract IV
        byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);

        // 3️⃣ Extract CipherText
        byte[] cipherText = Arrays.copyOfRange(
                combined,
                GCM_IV_LENGTH,
                combined.length
        );

        // 4️⃣ Rebuild SecretKey
        SecretKey secretKey = new SecretKeySpec(aesKeyBytes, "AES");

        // 5️⃣ Init Cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        // 6️⃣ Decrypt
        byte[] decrypted = cipher.doFinal(cipherText);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ==========================================================
    // Helper Result Class (Only One Field Now)
    // ==========================================================
    public static class AESResult {

        private final String cipherText; // IV + Cipher Combined

        public AESResult(String cipherText) {
            this.cipherText = cipherText;
        }

        public String getCipherText() {
            return cipherText;
        }
    }
}