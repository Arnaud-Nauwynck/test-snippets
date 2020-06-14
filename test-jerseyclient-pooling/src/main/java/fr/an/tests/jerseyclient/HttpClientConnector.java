package fr.an.tests.jerseyclient;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectionClosingStrategy;
import org.glassfish.jersey.apache.connector.ApacheConnectionClosingStrategy.GracefulClosingStrategy;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.client.spi.AsyncConnectorCallback;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.message.internal.HeaderUtils;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.glassfish.jersey.message.internal.Statuses;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultManagedHttpClientConnection;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.TextUtils;
import org.apache.http.util.VersionInfo;

/**
 * A {@link Connector} that utilizes the Apache HTTP Client to send and receive
 * HTTP request and responses.
 */
public class HttpClientConnector implements Connector {

    private static final Logger LOGGER = Logger.getLogger(HttpClientConnector.class.getName());

    private final CloseableHttpClient client;

//    private final CookieStore cookieStore;
//    private final boolean preemptiveBasicAuth;
    private final RequestConfig requestConfig;

    public HttpClientConnector(CloseableHttpClient client, RequestConfig requestConfig) {
        this.client = client;
        this.requestConfig = requestConfig;
    }



    @Override
    public ClientResponse apply(final ClientRequest clientRequest) throws ProcessingException {
        final HttpUriRequest request = getUriHttpRequest(clientRequest);
        final Map<String, String> clientHeadersSnapshot = writeOutBoundHeaders(clientRequest, request);

        try {
            final CloseableHttpResponse response;
            final HttpClientContext context = HttpClientContext.create();
//            if (preemptiveBasicAuth) {
//                final AuthCache authCache = new BasicAuthCache();
//                final BasicScheme basicScheme = new BasicScheme();
//                authCache.put(getHost(request), basicScheme);
//                context.setAuthCache(authCache);
//            }
//
//            // If a request-specific CredentialsProvider exists, use it instead of the default one
//            CredentialsProvider credentialsProvider =
//                    clientRequest.resolveProperty(ApacheClientProperties.CREDENTIALS_PROVIDER, CredentialsProvider.class);
//            if (credentialsProvider != null) {
//                context.setCredentialsProvider(credentialsProvider);
//            }

            response = client.execute(getHost(request), request, context);
            HeaderUtils.checkHeaderChanges(clientHeadersSnapshot, clientRequest.getHeaders(),
                    this.getClass().getName(), clientRequest.getConfiguration());

            final Response.StatusType status = response.getStatusLine().getReasonPhrase() == null
                    ? Statuses.from(response.getStatusLine().getStatusCode())
                    : Statuses.from(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());

            final ClientResponse responseContext = new ClientResponse(status, clientRequest);
            final List<URI> redirectLocations = context.getRedirectLocations();
            if (redirectLocations != null && !redirectLocations.isEmpty()) {
                responseContext.setResolvedRequestUri(redirectLocations.get(redirectLocations.size() - 1));
            }

            final Header[] respHeaders = response.getAllHeaders();
            final MultivaluedMap<String, String> headers = responseContext.getHeaders();
            for (final Header header : respHeaders) {
                final String headerName = header.getName();
                List<String> list = headers.get(headerName);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(header.getValue());
                headers.put(headerName, list);
            }

            final HttpEntity entity = response.getEntity();

            if (entity != null) {
                if (headers.get(HttpHeaders.CONTENT_LENGTH) == null) {
                    headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(entity.getContentLength()));
                }

                final Header contentEncoding = entity.getContentEncoding();
                if (headers.get(HttpHeaders.CONTENT_ENCODING) == null && contentEncoding != null) {
                    headers.add(HttpHeaders.CONTENT_ENCODING, contentEncoding.getValue());
                }
            }

            try {
                final ConnectionClosingMechanism closingMechanism = new ConnectionClosingMechanism(clientRequest, request);
                responseContext.setEntityStream(getInputStream(response, closingMechanism));
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }

            return responseContext;
        } catch (final Exception e) {
            throw new ProcessingException(e);
        }
    }

    @Override
    public Future<?> apply(final ClientRequest request, final AsyncConnectorCallback callback) {
        try {
            ClientResponse response = apply(request);
            callback.response(response);
            return CompletableFuture.completedFuture(response);
        } catch (Throwable t) {
            callback.failure(t);
            CompletableFuture<Object> future = new CompletableFuture<>();
            future.completeExceptionally(t);
            return future;
        }
    }

    @Override
    public String getName() {
        return "Apache HttpClient ";
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
        }
    }

    private HttpHost getHost(final HttpUriRequest request) {
        return new HttpHost(request.getURI().getHost(), request.getURI().getPort(), request.getURI().getScheme());
    }

    private HttpUriRequest getUriHttpRequest(final ClientRequest clientRequest) {
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.copy(requestConfig);

        final int connectTimeout = clientRequest.resolveProperty(ClientProperties.CONNECT_TIMEOUT, -1);
        final int socketTimeout = clientRequest.resolveProperty(ClientProperties.READ_TIMEOUT, -1);

        if (connectTimeout >= 0) {
            requestConfigBuilder.setConnectTimeout(connectTimeout);
        }
        if (socketTimeout >= 0) {
            requestConfigBuilder.setSocketTimeout(socketTimeout);
        }

        final Boolean redirectsEnabled =
                clientRequest.resolveProperty(ClientProperties.FOLLOW_REDIRECTS, requestConfig.isRedirectsEnabled());
        requestConfigBuilder.setRedirectsEnabled(redirectsEnabled);

        final Boolean bufferingEnabled = clientRequest.resolveProperty(ClientProperties.REQUEST_ENTITY_PROCESSING,
                RequestEntityProcessing.class) == RequestEntityProcessing.BUFFERED;
        final HttpEntity entity = getHttpEntity(clientRequest, bufferingEnabled);

        return RequestBuilder
                .create(clientRequest.getMethod())
                .setUri(clientRequest.getUri())
                .setConfig(requestConfigBuilder.build())
                .setEntity(entity)
                .build();
    }

    private HttpEntity getHttpEntity(final ClientRequest clientRequest, final boolean bufferingEnabled) {
        final Object entity = clientRequest.getEntity();

        if (entity == null) {
            return null;
        }

        final AbstractHttpEntity httpEntity = new AbstractHttpEntity() {
            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public long getContentLength() {
                return -1;
            }

            @Override
            public InputStream getContent() throws IOException, IllegalStateException {
                if (bufferingEnabled) {
                    final ByteArrayOutputStream buffer = new ByteArrayOutputStream(512);
                    writeTo(buffer);
                    return new ByteArrayInputStream(buffer.toByteArray());
                } else {
                    return null;
                }
            }

            @Override
            public void writeTo(final OutputStream outputStream) throws IOException {
                clientRequest.setStreamProvider(new OutboundMessageContext.StreamProvider() {
                    @Override
                    public OutputStream getOutputStream(final int contentLength) throws IOException {
                        return outputStream;
                    }
                });
                clientRequest.writeEntity();
            }

            @Override
            public boolean isStreaming() {
                return false;
            }
        };

        if (bufferingEnabled) {
            try {
                return new BufferedHttpEntity(httpEntity);
            } catch (final IOException e) {
                throw new ProcessingException("Error", e);
            }
        } else {
            return httpEntity;
        }
    }

    private static Map<String, String> writeOutBoundHeaders(final ClientRequest clientRequest,
                                                            final HttpUriRequest request) {
        final Map<String, String> stringHeaders =
                HeaderUtils.asStringHeadersSingleValue(clientRequest.getHeaders(), clientRequest.getConfiguration());

        for (final Map.Entry<String, String> e : stringHeaders.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        return stringHeaders;
    }

    private static InputStream getInputStream(final CloseableHttpResponse response,
                                              final ConnectionClosingMechanism closingMechanism) throws IOException {
        final InputStream inputStream;

        if (response.getEntity() == null) {
            inputStream = new ByteArrayInputStream(new byte[0]);
        } else {
            final InputStream i = response.getEntity().getContent();
            if (i.markSupported()) {
                inputStream = i;
            } else {
                inputStream = new BufferedInputStream(i, ReaderWriter.BUFFER_SIZE);
            }
        }

        return closingMechanism.getEntityStream(inputStream, response);
    }

    /**
     * The way the Apache CloseableHttpResponse is to be closed.
     * See https://github.com/eclipse-ee4j/jersey/issues/4321
     * {@link ApacheClientProperties#CONNECTION_CLOSING_STRATEGY}
     */
    private final class ConnectionClosingMechanism {
        private ApacheConnectionClosingStrategy connectionClosingStrategy = null;
        private final ClientRequest clientRequest;
        private final HttpUriRequest apacheRequest;

        private ConnectionClosingMechanism(ClientRequest clientRequest, HttpUriRequest apacheRequest) {
            this.clientRequest = clientRequest;
            this.apacheRequest = apacheRequest;
            Object closingStrategyProperty = clientRequest
                    .resolveProperty(ApacheClientProperties.CONNECTION_CLOSING_STRATEGY, Object.class);
            if (closingStrategyProperty != null) {
                if (ApacheConnectionClosingStrategy.class.isInstance(closingStrategyProperty)) {
                    connectionClosingStrategy = (ApacheConnectionClosingStrategy) closingStrategyProperty;
                } else {
                    LOGGER.log(Level.WARNING, "Error closing..");
                }
            }

            if (connectionClosingStrategy == null) {
                connectionClosingStrategy = 
                        GracefulClosingStrategy.INSTANCE;
                        // ApacheConnectionClosingStrategy.GracefulClosingStrategy.INSTANCE;
            }
        }

        private InputStream getEntityStream(final InputStream inputStream,
                                            final CloseableHttpResponse response) {
            InputStream filterStream = new FilterInputStream(inputStream) {
                @Override
                public void close() throws IOException {
                    connectionClosingStrategy.close(clientRequest, apacheRequest, response, in);
                }
            };
            return filterStream;
        }
    }

    static class GracefulClosingStrategy implements ApacheConnectionClosingStrategy {
        static final GracefulClosingStrategy INSTANCE = new GracefulClosingStrategy();

        @Override
        public void close(ClientRequest clientRequest, HttpUriRequest request, CloseableHttpResponse response, InputStream stream)
                throws IOException {
            if (response.getEntity() != null && response.getEntity().isChunked()) {
                request.abort();
            }
            try {
                stream.close();
            } catch (IOException ex) {
                // Ignore
            } finally {
                response.close();
            }
        }
    }

    
    private static class ConnectionFactory extends ManagedHttpClientConnectionFactory {

        private static final AtomicLong COUNTER = new AtomicLong();

        private final int chunkSize;

        private ConnectionFactory(final int chunkSize) {
            this.chunkSize = chunkSize;
        }

        @Override
        public ManagedHttpClientConnection create(final HttpRoute route, final ConnectionConfig config) {
            final String id = "http-outgoing-" + Long.toString(COUNTER.getAndIncrement());

            return new HttpClientConnection(id, config.getBufferSize(), chunkSize);
        }
    }

    private static class HttpClientConnection extends DefaultManagedHttpClientConnection {

        private final int chunkSize;

        private HttpClientConnection(final String id, final int buffersize, final int chunkSize) {
            super(id, buffersize);

            this.chunkSize = chunkSize;
        }

        @Override
        protected OutputStream createOutputStream(final long len, final SessionOutputBuffer outbuffer) {
            if (len == ContentLengthStrategy.CHUNKED) {
                return new ChunkedOutputStream(chunkSize, outbuffer);
            }
            return super.createOutputStream(len, outbuffer);
        }
    }
}
