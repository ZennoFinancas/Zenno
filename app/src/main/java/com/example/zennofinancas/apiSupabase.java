package com.example.zennofinancas;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class apiSupabase {
    private static final String SUPABASE_URL = "https://pmtlqpsdkgbmukqhwfed.supabase.co/auth/v1/signup";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBtdGxxcHNka2dibXVrcWh3ZmVkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTM0ODI4OTYsImV4cCI6MjA2OTA1ODg5Nn0.NeV7JuQPZrbHdvBGIgfKRMAIyIJN9oV9XcZeg-siHuQ";

    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public apiSupabase() {
        client = new OkHttpClient();
    }

    public String postData(String jsonData) {
        String json = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", "vini2@email.com", "vini1234");
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(SUPABASE_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Erro Inesperado " + response);
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
