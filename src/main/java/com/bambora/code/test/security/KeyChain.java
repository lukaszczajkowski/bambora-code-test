package com.bambora.code.test.security;

import com.bambora.code.test.utils.exceptions.TrustlyAPIException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Component
public class KeyChain {

    private final String publicKeyPath;
    private final String merchantPrivateKeyPath;
    private final String keyPassword;

    private PrivateKey merchantPrivateKey;
    private PublicKey trustlyPublicKey;

    public KeyChain(@Value("${trustly.public-key}") String publicKeyPath,
                    @Value("${trustly.private-key}") String merchantPrivateKeyPath) {
        this.publicKeyPath = publicKeyPath;
        this.merchantPrivateKeyPath = merchantPrivateKeyPath;
        this.keyPassword = "";
        loadTrustlyPublicKey();
    }

    void loadMerchantPrivateKey() throws KeyException {
        try {
            final File privateKeyFile = new File(merchantPrivateKeyPath);
            final PEMParser pemParser = new PEMParser(new FileReader(privateKeyFile));
            final Object object = pemParser.readObject();

            final PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(keyPassword.toCharArray());
            final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            final KeyPair kp;
            if (object instanceof PEMEncryptedKeyPair) {
                kp = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
            } else {
                kp = converter.getKeyPair((PEMKeyPair) object);
            }

            merchantPrivateKey = kp.getPrivate();
        }
        catch (final IOException e) {
            throw new KeyException("Failed to load private key", e);
        }
    }

    private void loadTrustlyPublicKey() {
        try {
            final File file = new File(publicKeyPath);
            final PEMParser pemParser = new PEMParser(new FileReader(file));
            final PemObject object = pemParser.readPemObject();

            final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider());

            final byte[] encoded = object.getContent();
            final SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(
                    ASN1Sequence.getInstance(encoded));

            trustlyPublicKey = converter.getPublicKey(subjectPublicKeyInfo);
        }
        catch (IOException e) {
            throw new TrustlyAPIException("Failed to load Trustly public key", e);
        }
    }

    public PrivateKey getMerchantPrivateKey() {
        return merchantPrivateKey;
    }

    public PublicKey getTrustlyPublicKey() {
        return trustlyPublicKey;
    }
}