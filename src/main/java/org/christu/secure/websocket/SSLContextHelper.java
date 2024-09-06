package org.christu.secure.websocket;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SSLContextHelper {

    public static SSLContext createSSLContextServer(String keystorePath, String keystorePassword, String truststorePath) throws Exception {
        // Load keystore (PKCS12 format)
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream keyStoreStream = new FileInputStream(keystorePath)) {
            keyStore.load(keyStoreStream, keystorePassword.toCharArray());
        }

        // Create key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keystorePassword.toCharArray());

        // Load truststore (PEM format)
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null, null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (InputStream trustStoreStream = new FileInputStream(truststorePath)) {
            X509Certificate caCert = (X509Certificate) cf.generateCertificate(trustStoreStream);
            trustStore.setCertificateEntry("ca-cert", caCert);
        }

        // Create trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Create SSL context
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }

}