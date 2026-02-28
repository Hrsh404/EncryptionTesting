package com.example.DepositAPI.DTO;



public class EncryptedRequest {

    private String encryptedKey;  // RSA encrypted AES key (Base64)
    private String iv;            // AES IV (Base64)
    private String cipherText;    // AES encrypted data (Base64)

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