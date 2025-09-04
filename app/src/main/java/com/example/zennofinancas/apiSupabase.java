package com.example.zennofinancas;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class apiSupabase {
    private static final String SUPABASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/usuarios";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";

    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json");

    public apiSupabase() {
        client = new OkHttpClient();
    }

    public String postData(String jsonData) {
        String json = String.format("{\"nome_user\":\"%s\", \"senha_user\":\"%s\"}", "Lucas Pereira", "vini166234");
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
