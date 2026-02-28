    package com.example.DepositAPI.security;



    import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
    import java.security.cert.Certificate;
    import java.security.cert.CertificateFactory;
    import java.security.spec.PKCS8EncodedKeySpec;
    import java.util.Base64;

    import org.springframework.core.io.ClassPathResource;
    import org.springframework.stereotype.Component;
    import org.springframework.util.FileCopyUtils;

 


;


@Component
public class KeyLoader {

    // Removed 'src/main/resources' because ClassPathResource looks there by default
    private static final String PRIVATE_KEY_RESOURCE = "keys/deposit_private_key.pem";
    private static final String LOANS_PUBLIC_KEY_RESOURCE = "keys/loans_public_cert.crt";

    // 🔐 Load Deposit Private Key
    public PrivateKey loadPrivateKey() throws Exception {
        // Load file from the classpath
        ClassPathResource resource = new ClassPathResource(PRIVATE_KEY_RESOURCE);
        
        byte[] bdata = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String key = new String(bdata, StandardCharsets.UTF_8);

        key = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return factory.generatePrivate(spec);
    }

    // 🔐 Load Loans Public Key (.crt)
    public PublicKey loadLoansPublicKey() throws Exception {
        ClassPathResource resource = new ClassPathResource(LOANS_PUBLIC_KEY_RESOURCE);

        try (InputStream is = resource.getInputStream()) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate certificate = factory.generateCertificate(is);

            return certificate.getPublicKey();
        }
    }

// 🔐 Load Deposit Public Key (.crt)
// 🔐 Load Deposit Public Key (.crt)
public PublicKey loadDepositPublicKey() throws Exception {
    // Look directly in the resources/keys folder
    String resourcePath = "keys/deposit_public_cert.crt";
    ClassPathResource resource = new ClassPathResource(resourcePath);

    try (InputStream is = resource.getInputStream()) {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate certificate = factory.generateCertificate(is);

        return certificate.getPublicKey();
    }
}


}