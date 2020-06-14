package fr.an.tests.httpcli;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class HttpJdk11Main {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        testAsyncHttpGet(client);
        testSyncHttpGet(client);

        // testing through http Proxy
        HttpClient clientProxySocks = HttpClient.newBuilder()
                .proxy(ProxySelector.of(new InetSocketAddress("localhost", 3128)))
                .build();
        testSyncHttpGet(clientProxySocks);

    }

    public static void testSyncHttpGet(HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://openjdk.java.net/"))
                .build();
        String body = client.send(request, BodyHandlers.ofString())
            .body();
        System.out.println("sync http GET => len:" + body.length() + " ");
    }

    public static void testAsyncHttpGet(HttpClient client) {
        // async http GET
        HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create("http://openjdk.java.net/"))
              .build();
        client.sendAsync(request, BodyHandlers.ofString())
              .thenApply(HttpResponse::body)
              .thenAccept(x -> {
                  System.out.println("async http GET => len:" + x.length() + " "
                          + "(thread:" + Thread.currentThread().getName() + ")");
              })
              .join();
    }
}
