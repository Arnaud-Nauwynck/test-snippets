package fr.an.tests.jerseyclient;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.spi.ConnectorProvider;

public class AppMain {

    public static void main(String[] args) {
        try { 
            new AppMain().run();
        } catch(Exception ex) {
            System.err.println("Failed");
            throw new RuntimeException("Failed", ex);
        }
    }
    
    public void run() {
        Client jerseyClient = 
                createJerseyClient2();
//                createJerseyClient();
   
        testHttpGet(jerseyClient);
        testHttpGet(jerseyClient);
        
    }

    private void testHttpGet(Client jerseyClient) {
        Invocation req = jerseyClient.target(
                // "https://www.iut.univ-paris8.fr/"
                "https://www.google.fr"
                )
            .request()
            .buildGet();
        Response resp = req.invoke();
        InputStream respEntity = (InputStream) resp.getEntity();
        String entityText;
        try {
            entityText = IOUtils.toString(respEntity);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read");
        }
        System.out.println("resp " + entityText.length() + " " + entityText.substring(0, 50) + " ..");
    }
    
    public Client createJerseyClient() {
        ClientConfig jerseyClientConfig = new ClientConfig();
        jerseyClientConfig.property(ClientProperties.READ_TIMEOUT, 10000);
        jerseyClientConfig.property(ClientProperties.CONNECT_TIMEOUT, 5000);

        PoolingHttpClientConnectionManager httpCliConnectionManager = new PoolingHttpClientConnectionManager();
        httpCliConnectionManager.setMaxTotal(100);
        httpCliConnectionManager.setDefaultMaxPerRoute(20);
        httpCliConnectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("localhost")), 40);

        jerseyClientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, 
                httpCliConnectionManager);

        jerseyClientConfig.property(ApacheClientProperties.KEEPALIVE_STRATEGY, 
                DefaultConnectionKeepAliveStrategy.INSTANCE);

        jerseyClientConfig.property(ApacheClientProperties.REUSE_STRATEGY, 
                DefaultConnectionReuseStrategy.INSTANCE
                // DefaultClientConnectionReuseStrategy.INSTANCE
                );

        
        ConnectorProvider connectorProvider = new ApacheConnectorProvider();
        jerseyClientConfig.connectorProvider(connectorProvider);

//        CachingConnectorProvider wrapCachingProvider = new CachingConnectorProvider(
//                connectorProvider);
        
//        jerseyClientConfig.property(ApacheClientProperties.
        // Proxy netProxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", 3128));
        jerseyClientConfig.property( ClientProperties.PROXY_URI, 
                "https://localhost:3128" ); // type SOCKS ???
                
        Client jerseyClient = ClientBuilder.newClient(jerseyClientConfig);
        // client.register(JacksonFeature.class);
        // jerseyClient.
        return jerseyClient;
    }

    public CloseableHttpClient createHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();

        builder.setMaxConnPerRoute(20); // cf PoolingHttpClientConnectionManager
        
        HttpHost proxy = new HttpHost("localhost", 3128, 
                "socks"
                );
        builder.setProxy(proxy);
        
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(30000)
                // .setProxy(proxy)
                .build();
        
        builder.setDefaultRequestConfig(requestConfig);
        
        PoolingHttpClientConnectionManager httpCliConnectionManager = new PoolingHttpClientConnectionManager();
        httpCliConnectionManager.setMaxTotal(100);
        httpCliConnectionManager.setDefaultMaxPerRoute(20);
        httpCliConnectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("localhost")), 40);
        builder.setConnectionManager(httpCliConnectionManager);
        
        CloseableHttpClient httpClient = builder.build();
        return httpClient;
    }
    
    public Client createJerseyClient2() {
        CloseableHttpClient apacheHttpCli = createHttpClient();
        RequestConfig requestConfig = RequestConfig.DEFAULT;
        
        ClientConfig jerseyClientConfig = new ClientConfig();

        HttpClientConnector httpCliConnectionManager = 
                new HttpClientConnector(apacheHttpCli, requestConfig);
        jerseyClientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, 
                httpCliConnectionManager);

        // ??
        jerseyClientConfig.property(ClientProperties.READ_TIMEOUT, 10000);
        jerseyClientConfig.property(ClientProperties.CONNECT_TIMEOUT, 5000);


//        jerseyClientConfig.property(ApacheClientProperties.KEEPALIVE_STRATEGY, 
//                DefaultConnectionKeepAliveStrategy.INSTANCE);
//
//        jerseyClientConfig.property(ApacheClientProperties.REUSE_STRATEGY, 
//                DefaultConnectionReuseStrategy.INSTANCE
//                // DefaultClientConnectionReuseStrategy.INSTANCE
//                );
        
        Client jerseyClient = ClientBuilder.newClient(jerseyClientConfig);
        // client.register(JacksonFeature.class);
        return jerseyClient;
    }

}
