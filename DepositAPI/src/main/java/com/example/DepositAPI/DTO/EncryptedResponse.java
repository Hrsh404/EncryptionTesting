package com.example.DepositAPI.DTO;



public class EncryptedResponse {

    private String encryptedKey;
    private String iv;
    private String cipherText;

    public EncryptedResponse() {}

    public EncryptedResponse(String encryptedKey, String iv, String cipherText) {
        this.encryptedKey = encryptedKey;
        this.iv = iv;
        this.cipherText = cipherText;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }
}