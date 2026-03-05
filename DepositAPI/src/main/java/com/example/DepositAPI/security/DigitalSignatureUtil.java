package com.example.DepositAPI.security;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import org.springframework.stereotype.Component;
@Component
public class DigitalSignatureUtil {

    private static final String SIGN_ALGO = "SHA256withRSA";

    // 🔏 Sign Data
    public String sign(String data, PrivateKey privateKey) throws Exception {

        Signature signature = Signature.getInstance(SIGN_ALGO);
        signature.initSign(privateKey);
        signature.update(data.getBytes());

        byte[] signedBytes = signature.sign();

        return Base64.getEncoder().encodeToString(signedBytes);
    }

    // 🔍 Verify Signature
    public boolean verify(String data, String signatureStr, PublicKey publicKey) throws Exception {

        Signature signature = Signature.getInstance(SIGN_ALGO);
        signature.initVerify(publicKey);
        signature.update(data.getBytes());

        byte[] signedBytes = Base64.getDecoder().decode(signatureStr);

        return signature.verify(signedBytes);
    }
}