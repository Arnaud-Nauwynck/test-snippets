package fr.an.tools.flipbook2pdf.impl;

import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import fr.an.tools.flipbook2pdf.utils.TrustAllX509TrustManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

@Slf4j
public class HttpClientHelper {

    private static final String PATH_DMDPDFSTAMPER_PDFS = "/module/dmdpdfstamper/pdfs";
    private static final String PATH_FLIPBBOOKS = "/modules/dmdpdfstamper/flipbooks/";
    private static final String PATH_FILES_MOBILE = "/files/mobile/";
    private static final String PATH_MOBILE_JAVASCRIPT_CONFIGJS = "/mobile/javascript/config.js";
    
    private String baseUrl = "https://boutique.ed-diamond.com";

    private OkHttpClient httpClient;
        
    public HttpClientHelper() {
    }
    
    public void initHttpClient(Flipbook2PdfParams params) throws Exception {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    
        // cookie manager
        httpClientBuilder.cookieJar(new JavaNetCookieJar(new CookieManager()));
        
        if (params.isDebugHttpCalls()) {
            // init okhttp 3 logger
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            httpClientBuilder.addInterceptor(logging);
        }
                
        if (params.getHttpProxyHost() != null) {
            Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(params.getHttpProxyHost(), params.getHttpProxyPort()));
            httpClientBuilder.proxy(proxy);
        }
                
        if (params.isTrustAllCerts()) {
            TrustAllX509TrustManager trustAllX509TrustManager = new TrustAllX509TrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = new TrustManager[]{trustAllX509TrustManager};
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            httpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), trustAllX509TrustManager);

            httpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
        
        this.httpClient = httpClientBuilder.build();
    }
    
    public void httpAuthenticate(String authEmail, String authPasswd) {
        String authUrl = baseUrl + "/authentification";

        FormBody authReqBody = new FormBody.Builder()
              .addEncoded("email", authEmail)
              .addEncoded("passwd", authPasswd)
              .addEncoded("back", "my-account")
              .addEncoded("SubmitLogin", "")
              .build();        
        
        Request authRequest = new Request.Builder()
                .url(authUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Cache-Control", "max-age=0")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-User", "?1")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Origin", baseUrl)
                .header("Referer", baseUrl + "/authentification?back=my-account")
                .post(authReqBody)
                .build();
        try (Response response = httpClient.newCall(authRequest).execute()) {
            if (response.isRedirect() || response.isSuccessful()) {
                log.info("authenticated..");
            } else {
                throw new RuntimeException("Failed to authenticate");
            }
        } catch(Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
    }

    public String downloadBooksHtmlPage() {
        String url = baseUrl + PATH_DMDPDFSTAMPER_PDFS;
        log.info("scanBooksPage: http query " + url);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "*/*")
//                .header("Sec-Fetch-Mode", "no-cors")
//                .header("Sec-Fetch-Site", "same-origin")
//                .header("Referer", baseUrl + "/module/dmdpdfstamper/read?id_order_detail=" + orderDetail)
                .build();
        String respContent;
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody respBody = response.body();
                respContent = respBody.string();
            } else {
                throw new RuntimeException("Failed to get book config: " + url);
            }
        } catch(Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
        return respContent;
    }
    
    public String downloadBookConfig(String bookTitle, String orderDetail) {
        String url = baseUrl + PATH_FLIPBBOOKS + bookTitle + PATH_MOBILE_JAVASCRIPT_CONFIGJS;
        log.info("http query " + url);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "*/*")
                .header("Sec-Fetch-Mode", "no-cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Referer", baseUrl + "/module/dmdpdfstamper/read?id_order_detail=" + orderDetail)
                .build();
        String respContent;
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody respBody = response.body();
                respContent = respBody.string();
            } else {
                throw new RuntimeException("Failed to get book config: " + url);
            }
        } catch(Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
        return respContent;
    }

    public byte[] httpDownloadPageImage(DownloadBookCtx ctx, String imageName) {
        String url = baseUrl + PATH_FLIPBBOOKS + ctx.bookTitle + PATH_FILES_MOBILE + imageName +"?" + ctx.bookConfig.getCreatedTime();
        log.info("download image " + imageName + ": http query " + url);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "image/*")
                .header("Sec-Fetch-Dest", "image")
                .header("Sec-Fetch-Mode", "no-cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Referer", baseUrl + "/module/dmdpdfstamper/read?id_order_detail=" + ctx.orderDetail)
                .build();
        byte[] pageImgContent;
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody respBody = response.body();
                pageImgContent = respBody.bytes();
                if (pageImgContent.length == 0) {
                    throw new RuntimeException("Failed to download image " + imageName + " - empty image!");
                }
            } else {
                throw new RuntimeException("Failed to download image " + imageName + " " + response.code());
            }
        } catch(Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
        return pageImgContent;
    }

    public String downloadHtml(String url, String orderDetailId) {
        log.info("http query " + url);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "*/*")
                .header("Sec-Fetch-Mode", "no-cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Referer", baseUrl + "/module/dmdpdfstamper/read?id_order_detail=" + orderDetailId)
                .build();
        String respContent;
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody respBody = response.body();
                respContent = respBody.string();
            } else {
                throw new RuntimeException("Failed to get url: " + url);
            }
        } catch(Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
        return respContent;
        
    }
    
}
