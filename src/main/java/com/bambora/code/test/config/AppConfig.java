package com.bambora.code.test.config;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public PrivateKey loadClientPrivateKey(@Value("${trustly.private-key}") String privateKeyFilename,
                                           @Value("${trustly.api-password}") String password) throws KeyException {

        try {
            File privateKeyFile = new File(privateKeyFilename);
            PEMParser pemParser = new PEMParser(new FileReader(privateKeyFile));
            Object object = pemParser.readObject();

            PEMDecryptorProvider decryptorProvider =
                    new JcePEMDecryptorProviderBuilder()
                            .build(password.toCharArray());
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            KeyPair kp;
            if(object instanceof PEMEncryptedKeyPair) {
                kp = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decryptorProvider));
            } else {
                kp = converter.getKeyPair((PEMKeyPair) object);
            }

            PrivateKey clientPrivateKey = kp.getPrivate();
            return clientPrivateKey;
        } catch (IOException e) {
            throw new KeyException("Could not load private key", e);
        }
    }

    @Bean
    public PublicKey loadTrustlyPublicKey(@Value("${trustly.public-key}") String publicKeyPath) {
        try {
            File file = new File(publicKeyPath);
            PEMParser pemParser = new PEMParser(new FileReader(file));
            PemObject object = pemParser.readPemObject();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter()
                    .setProvider(new BouncyCastleProvider());

            byte[] encoded = object.getContent();
            SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(ASN1Sequence.getInstance(encoded));

            PublicKey trustlyPublicKey = converter.getPublicKey(subjectPublicKeyInfo);
            return trustlyPublicKey;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
