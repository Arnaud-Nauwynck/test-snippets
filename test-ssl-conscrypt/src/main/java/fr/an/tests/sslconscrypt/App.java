package fr.an.tests.sslconscrypt;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Provider;
import java.security.Security;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.conscrypt.OpenSSLProvider;

/**
 *
 */
public class App {

    public static void main(String[] args) throws Exception {

        installConscryptAsDefaultProvider();
        
        SSLContext defaultSslContext = SSLContext.getDefault();
        System.out.println("default TLS provider: " + defaultSslContext.getProvider());
        
        URL url = new URL("https://www.google.fr");
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        try (InputStream contentIn = urlCon.getInputStream()) {
            String contentStr = IOUtils.toString(contentIn, "UTF-8");
            System.out.println("resp.. " + contentStr.substring(0, 20) + " ..");
        }
        
    }

    public static synchronized void installConscryptAsDefaultProvider() {
        String providerName = "Conscrypt"; // Platform.getDefaultProviderName()
        OpenSSLProvider conscryptProvider = new OpenSSLProvider(providerName);

        Provider[] providers = Security.getProviders();
        if (providers.length == 0 || !providers[0].equals(conscryptProvider)) {
            Security.insertProviderAt(conscryptProvider, 1);
        }
    }

}
