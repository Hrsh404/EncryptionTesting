package com.example.DepositAPI.DTO;


public class EncryptedResponse {

    private String encryptedKey;
    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    private String cipherText;
    private String signature;

    public EncryptedResponse() {}

    public EncryptedResponse(String encryptedKey,
                             String cipherText,
                             String signature) {
        this.encryptedKey = encryptedKey;
        this.cipherText = cipherText;
        this.signature = signature;
    }

    // getters & setters
}