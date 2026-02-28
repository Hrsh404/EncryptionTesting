package com.example.DepositAPI.security;



import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class RSATest {

    public static void main(String[] args) throws Exception {

        RSAUtil rsaUtil = new RSAUtil();

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        byte[] sampleAESKey = "1234567890123456".getBytes();

        // Encrypt
        String encrypted = rsaUtil.encryptAESKey(sampleAESKey, pair.getPublic());

        // Decrypt
        byte[] decrypted = rsaUtil.decryptAESKey(encrypted, pair.getPrivate());

        System.out.println("Original: " + new String(sampleAESKey));
        System.out.println("Decrypted: " + new String(decrypted));
    }
}