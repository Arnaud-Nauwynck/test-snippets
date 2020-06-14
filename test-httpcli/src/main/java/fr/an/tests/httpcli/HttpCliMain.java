package fr.an.tests.httpcli;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import fr.an.tests.httpcli.MyConnectionSocketFactory.MySSLConnectionSocketFactory;

public class HttpCliMain {

    public static void main(String[] args) {
        try { 
            new HttpCliMain().run();
        } catch(Exception ex) {
            System.err.println("Failed");
            throw new RuntimeException("Failed", ex);
        }
    }
    
    public void run() {
        CloseableHttpClient httpClient = createHttpClient();
   
        testHttpGet(httpClient);
        testHttpGet(httpClient);
        
    }

    private void testHttpGet(CloseableHttpClient httpClient) {
        String entityText;

        HttpUriRequest req = new HttpGet("https://www.google.fr");
        
        InetSocketAddress socksaddr = new InetSocketAddress("localhost", 3128);
        HttpClientContext httpCliContext = HttpClientContext.create();
        httpCliContext.setAttribute("socks.address", socksaddr);
    
        CloseableHttpResponse resp = null;
        try {
            resp = httpClient.execute(req, httpCliContext);
            
            HttpEntity entity = resp.getEntity();
            entityText = EntityUtils.toString(entity);
                        
        } catch (Exception ex) {
            throw new RuntimeException("", ex);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch(Exception ex) {
                }
            }
        }

        System.out.println("resp " + entityText.length() + " " + entityText.substring(0, 50) + " ..");
    }
    
    public CloseableHttpClient createHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();

        builder.setMaxConnPerRoute(20); // cf PoolingHttpClientConnectionManager
        
//        HttpHost proxy = new HttpHost("localhost", 3128, 
//                "socks" ... NOT SUPPORTED !!!
//                );
//        builder.setProxy(proxy);
        
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


        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new MyConnectionSocketFactory())
                .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault()))
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
        builder.setConnectionManager(cm);

        CloseableHttpClient httpClient = builder.build();
        return httpClient;
    }
    
}


class MyConnectionSocketFactory implements ConnectionSocketFactory {

    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
        return new Socket(proxy);
    }

    @Override
    public Socket connectSocket(
            final int connectTimeout,
            final Socket socket,
            final HttpHost host,
            final InetSocketAddress remoteAddress,
            final InetSocketAddress localAddress,
            final HttpContext context) throws IOException, ConnectTimeoutException {
        Socket sock;
        if (socket != null) {
            sock = socket;
        } else {
            sock = createSocket(context);
        }
        if (localAddress != null) {
            sock.bind(localAddress);
        }
        try {
            sock.connect(remoteAddress, connectTimeout);
        } catch (SocketTimeoutException ex) {
            throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
        }
        return sock;
    }

    
    
    static class MySSLConnectionSocketFactory extends SSLConnectionSocketFactory {

        public MySSLConnectionSocketFactory(final SSLContext sslContext) {
            // You may need this verifier if target site's certificate is not secure
            super(sslContext, ALLOW_ALL_HOSTNAME_VERIFIER);
        }

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }

        @Override
        public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                InetSocketAddress localAddress, HttpContext context) throws IOException {
            // Convert address to unresolved
            InetSocketAddress unresolvedRemote = InetSocketAddress
                    .createUnresolved(host.getHostName(), remoteAddress.getPort());
            return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
        }
    }
}