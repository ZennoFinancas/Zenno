package com.example.zennofinancas.api;

import android.content.Context;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class clsSupabaseClient {
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    private static final String BASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";

    public static void get(Context context, String endpoint, FutureCallback<JsonArray> callback) {
        Ion.with(context)
                .load("GET", BASE_URL + endpoint)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray()
                .setCallback(callback);
    }

    public static void post(Context context, String endpoint, JsonObject body, FutureCallback<String> callback) {
        Ion.with(context)
                .load("POST", BASE_URL + endpoint)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(body)
                .asString()
                .setCallback(callback);
    }

    public static void patch(Context context, String endpoint, JsonObject body, FutureCallback<JsonArray> callback) {
        Ion.with(context)
                .load("PATCH", BASE_URL + endpoint)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .setJsonObjectBody(body)
                .asJsonArray()
                .setCallback(callback);
    }
}
