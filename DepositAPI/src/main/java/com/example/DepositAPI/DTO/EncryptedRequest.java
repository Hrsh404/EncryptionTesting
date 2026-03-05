package com.example.DepositAPI.DTO;



public class EncryptedRequest {

       private String encryptedKey;
    private String cipherText;   // IV + Cipher combined
    private String signature;
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
} 