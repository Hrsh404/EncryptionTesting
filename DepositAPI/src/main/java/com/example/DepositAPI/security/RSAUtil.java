package com.example.DepositAPI.security;



import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

import org.springframework.stereotype.Component;

@Component
public class RSAUtil {

    private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    // 🔐 Encrypt AES key using Public Key
    public String encryptAESKey(byte[] aesKey, PublicKey publicKey) throws Exception {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encrypted = cipher.doFinal(aesKey);

        return Base64.getEncoder().encodeToString(encrypted);
    }

    // 🔓 Decrypt AES key using Private Key
    public byte[] decryptAESKey(String encryptedKey, PrivateKey privateKey) throws Exception {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decoded = Base64.getDecoder().decode(encryptedKey);

        return cipher.doFinal(decoded);
    }
}