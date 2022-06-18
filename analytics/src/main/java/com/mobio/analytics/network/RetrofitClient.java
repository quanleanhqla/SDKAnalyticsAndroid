package com.mobio.analytics.network;

import com.mobio.analytics.BuildConfig;
import com.mobio.analytics.client.utility.LogMobio;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.zip.Deflater;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.DeflaterSink;
import okio.GzipSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private Api myApi;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient().build())
                .build();
        myApi = retrofit.create(Api.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public Api getMyApi() {
        return myApi;
    }

    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {

            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            builder.addInterceptor(new ErrorInterceptor());
//            builder.addInterceptor(new CompressionRequestInterceptor());
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class ErrorInterceptor implements Interceptor {
        int maxLimit = 3;
        int waitThreshold = 5000;

        @Inject
        public ErrorInterceptor() { }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;
            boolean responseOK = false;
            int tryCount = 0;

            while (!responseOK && tryCount < maxLimit) {
                try {
                    response = chain.proceed(request);
                    responseOK = response.isSuccessful();
                }catch (Exception e){
<<<<<<< HEAD
                    e.printStackTrace();
                }finally{
//                    if(!responseOK){
//                        try {
//                            Thread.sleep(waitThreshold);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    tryCount++;
=======
                    LogMobio.logD("intercept", "Request is not successful - " + tryCount);
                }finally{
                    LogMobio.logD("intercept", "Request count - " + tryCount);
                    tryCount++;
//                    try {
//                        Thread.sleep(waitThreshold);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
>>>>>>> 54b8c3df2c3c49a849d06d7e38d9f17cba2587b8
                }
            }

            // otherwise just pass the original response on
            return response;
        }
    }

    static class CompressionRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                return chain.proceed(originalRequest);
            }

            Request compressedRequest = originalRequest.newBuilder()
                    .header("Content-Encoding", "gzip")
                    .method(originalRequest.method(), forceContentLength(gzip(originalRequest.body())))
                    .build();

<<<<<<< HEAD
=======
            LogMobio.logD("QuanLA", "size " + compressedRequest.body().contentLength());
>>>>>>> 54b8c3df2c3c49a849d06d7e38d9f17cba2587b8
            return chain.proceed(compressedRequest);
        }

        private RequestBody forceContentLength(final RequestBody requestBody) throws IOException {
            final Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return requestBody.contentType();
                }

                @Override
                public long contentLength() {
                    return buffer.size();
                }

                @Override
                public void writeTo(final BufferedSink sink) throws IOException {
                    sink.write(buffer.snapshot());
                }
            };
        }

        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override
                public void writeTo(final BufferedSink sink) throws IOException {
                    final BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }

        private RequestBody deflate(final RequestBody body) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override
                public void writeTo(final BufferedSink sink) throws IOException {
                    final BufferedSink deflateSink = Okio.buffer(new DeflaterSink(sink, new Deflater()));
                    body.writeTo(deflateSink);
                    deflateSink.close();
                }
            };
        }
    }
}
