package com.example.zennofinancas;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class apiSupabase {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
