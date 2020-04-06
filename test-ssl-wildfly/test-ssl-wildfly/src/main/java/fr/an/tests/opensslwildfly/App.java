package fr.an.tests.opensslwildfly;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.wildfly.openssl.OpenSSLProvider;

/**
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        OpenSSLProvider.register();

        SSLContext instance = SSLContext.getInstance("openssl.TLS");
        instance.init(null, null, new SecureRandom());
        SSLContext.setDefault(instance);

        URL url = new URL("https://www.google.fr");
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        try (InputStream contentIn = urlCon.getInputStream()) {
            String contentStr = IOUtils.toString(contentIn, "UTF-8");
            System.out.println("resp.. " + contentStr.substring(0, 20) + " ..");
        }

    }
}
