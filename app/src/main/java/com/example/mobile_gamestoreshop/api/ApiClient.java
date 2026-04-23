package com.example.mobile_gamestoreshop.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://10.0.2.2:7083/";
    private static final String TAG = "ApiClient";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("Accept", "application/json")
                                    .method(original.method(), original.body());

                            if (context != null) {
                                SharedPreferences prefs = context.getSharedPreferences("GameShopPrefs", Context.MODE_PRIVATE);
                                String token = prefs.getString("user_token", null);
                                if (token != null && !token.isEmpty()) {
                                    requestBuilder.header("Authorization", "Bearer " + token);
                                }
                            }

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    });

            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                };

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new SecureRandom());

                HostnameVerifier hostnameVerifier = (hostname, session) -> true;

                httpClient.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
                httpClient.hostnameVerifier(hostnameVerifier);

            } catch (Exception e) {
                Log.e(TAG, "SSL configuration error: " + e.getMessage(), e);
            }

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService(Context context) {
        if (apiService == null) {
            apiService = getClient(context).create(ApiService.class);
        }
        return apiService;
    }

    public static void resetClient() {
        retrofit = null;
        apiService = null;
    }
}