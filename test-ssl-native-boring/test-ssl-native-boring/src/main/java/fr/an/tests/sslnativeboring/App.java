package fr.an.tests.sslnativeboring;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.reactivestreams.Publisher;

import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;

import io.netty.buffer.ByteBuf;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.ProxyProvider;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        SslContext sslContext = SslContextBuilder
                // .forServer(certificate, privateKey)
                .forClient().sslProvider(SslProvider.OPENSSL)
                // .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        ConnectionProvider connectionProvider = null;

        Consumer<? super ProxyProvider.TypeSpec> nettyProxyOptions = null;
            // (opts) -> { ops.type(ProxyProvider.Proxy.HTTP).host("localhost").port(1234); }
        
        // new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("localhost",
        // 3128)
        // (proxy) -> {
        //        .address(proxyOptions.getAddress())
        //        .username(proxyOptions.getUsername())
        //        .password(userName -> proxyOptions.getPassword()).build())
        // }

        HttpClient reactorNettyHttpClient;
        if (connectionProvider != null) {
            reactorNettyHttpClient = HttpClient.create(connectionProvider);
        } else {
            reactorNettyHttpClient = HttpClient.create();
        }
        reactorNettyHttpClient = reactorNettyHttpClient.secure(ssl -> {
            ssl.sslContext(sslContext);
        })
//            .wiretap(enableWiretap)
                .tcpConfiguration(tcpConfig -> {
//                  if (nioEventLoopGroup != null) {
//                    tcpConfig = tcpConfig.runOn(nioEventLoopGroup);
//                }
                    if (nettyProxyOptions != null) {
                        // tcpConfig = tcpConfig.proxy(nettyProxyOptions);
                    }
                    return tcpConfig;
                });

        String baseUri = "https://www.google.fr";

        HttpResponse resp = reactorNettyHttpClient.baseUrl(baseUri)
                .get()
                .responseConnection(responseDelegate(null)) // cf wrapper ReactorNettyHttpResponse
                .blockFirst();

        if (resp.getStatusCode() / 100 == 2) {
            // OK!
            String respBody = resp.getBodyAsString().block();
            System.out.println("resp.. " + respBody.substring(0, 20) + " ..");
        } else {
            System.err.println("error .. " + resp.getStatusCode());
        }

    }

    // ------------------------------------------------------------------------

    // cf wrapper code in azure..

//  com.azure.core.http.HttpClient azureHttpClient = new NettyAsyncHttpClientBuilder()
//  .proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("localhost", 3128))
//      .setCredentials("example", "weakPassword"))
//  .build();

    /**
     * Delegate to receive response.
     *
     * @param restRequest the Rest request whose response this delegate handles
     * @return a delegate upon invocation setup Rest response object
     */
    private static BiFunction<HttpClientResponse, Connection, Publisher<HttpResponse>> responseDelegate(
            final HttpRequest restRequest) {
        return (reactorNettyResponse, reactorNettyConnection) -> Mono
                .just(new ReactorNettyHttpResponse(reactorNettyResponse, reactorNettyConnection, restRequest));
    }

    static class ReactorNettyHttpResponse extends HttpResponse {
        private final HttpClientResponse reactorNettyResponse;
        private final Connection reactorNettyConnection;

        ReactorNettyHttpResponse(HttpClientResponse reactorNettyResponse, Connection reactorNettyConnection,
                HttpRequest httpRequest) {
            super(httpRequest);
            this.reactorNettyResponse = reactorNettyResponse;
            this.reactorNettyConnection = reactorNettyConnection;
        }

        @Override
        public int getStatusCode() {
            return reactorNettyResponse.status().code();
        }

        @Override
        public String getHeaderValue(String name) {
            return reactorNettyResponse.responseHeaders().get(name);
        }

        @Override
        public HttpHeaders getHeaders() {
            HttpHeaders headers = new HttpHeaders();
            reactorNettyResponse.responseHeaders().forEach(e -> headers.put(e.getKey(), e.getValue()));
            return headers;
        }

        @Override
        public Flux<ByteBuffer> getBody() {
            return bodyIntern().doFinally(s -> {
                if (!reactorNettyConnection.isDisposed()) {
                    reactorNettyConnection.channel().eventLoop().execute(reactorNettyConnection::dispose);
                }
            }).map(ByteBuf::nioBuffer);
        }

        @Override
        public Mono<byte[]> getBodyAsByteArray() {
            return bodyIntern().aggregate().asByteArray().doFinally(s -> {
                if (!reactorNettyConnection.isDisposed()) {
                    reactorNettyConnection.channel().eventLoop().execute(reactorNettyConnection::dispose);
                }
            });
        }

        @Override
        public Mono<String> getBodyAsString() {
            return bodyIntern().aggregate().asString().doFinally(s -> {
                if (!reactorNettyConnection.isDisposed()) {
                    reactorNettyConnection.channel().eventLoop().execute(reactorNettyConnection::dispose);
                }
            });
        }

        @Override
        public Mono<String> getBodyAsString(Charset charset) {
            return bodyIntern().aggregate().asString(charset).doFinally(s -> {
                if (!reactorNettyConnection.isDisposed()) {
                    reactorNettyConnection.channel().eventLoop().execute(reactorNettyConnection::dispose);
                }
            });
        }

        @Override
        public void close() {
            if (!reactorNettyConnection.isDisposed()) {
                reactorNettyConnection.channel().eventLoop().execute(reactorNettyConnection::dispose);
            }
        }

        private ByteBufFlux bodyIntern() {
            return reactorNettyConnection.inbound().receive();
        }

        // used for testing only
        Connection internConnection() {
            return reactorNettyConnection;
        }
    }
}
