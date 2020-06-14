package fr.an.tests.httpnettyaz;

import java.net.InetSocketAddress;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpMethod;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;

public class NettyHttpAzureMain {

    public static void main(String[] args) {
        new NettyHttpAzureMain().run();
    }

    private void run() {
        NettyAsyncHttpClientBuilder builder = new NettyAsyncHttpClientBuilder();
        
//        Configuration configuration = new Configuration();
//        // configuration.
//        builder.configuration(configuration);
        
        builder.proxy(new ProxyOptions(ProxyOptions.Type.SOCKS5, new InetSocketAddress("localhost", 3128)));        
        
        HttpClient azHttpClient = builder.build();
        
        testHttpGet(azHttpClient);
        testHttpGet(azHttpClient);
        
    }

    private void testHttpGet(HttpClient azHttpClient) {
        String url = "https://www.google.fr";
        HttpRequest req = new HttpRequest(HttpMethod.GET, url);
        HttpResponse resp = azHttpClient.send(req).block();
        String respText = resp.getBodyAsString().block();
        System.out.println("GET => length:" + respText.length() + " " + respText.substring(0, 20) + " ..");
    }

}
