package com.example.DepositAPI.security;


import javax.crypto.SecretKey;

public class AESTest {

    public static void main(String[] args) throws Exception {

        AESUtil aesUtil = new AESUtil();

        SecretKey key = aesUtil.generateAESKey();

        String message = "Hello Hybrid Encryption!";

        AESUtil.AESResult result = aesUtil.encrypt(message, key);

        String decrypted = aesUtil.decrypt(
                result.getCipherText(),
                result.getIv(),
                key.getEncoded()
        );

        System.out.println("Original: " + message);
        System.out.println("Decrypted: " + decrypted);
    }
}