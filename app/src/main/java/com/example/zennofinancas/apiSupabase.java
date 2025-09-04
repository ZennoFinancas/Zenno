package com.example.zennofinancas;

import android.content.Intent;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class apiSupabase {
    String Host = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";
    private String url,ret;
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


    /*private void logar()
    {
        url=Host+"usuarios";
        Ion.with (TelaEntrar.this)
                .load ( url )
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setBodyParameter ( "nome_user" ,"Pedro Costa")
                .setBodyParameter ( "email_user" ,"pcp161107@gmail.com")
                .setBodyParameter ( "senha_user","pcp123")
                .asJsonObject ()
                .setCallback ( new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        ret=result.get("status").getAsString ();
                        if(ret.equals ("ok"))
                        {
                            //  Toast.makeText(getApplicationContext(),
                            //  " login e senha ok",   Toast.LENGTH_LONG).show();
                            fotox=result.get("foto").getAsString ().toString();


                            loginx=txtlogin.getText().toString();
                            senhax=txtsenha.getText().toString();
                            Intent trocar=new Intent(TelaEntrar.this,
                                    TelaEntrar2.class);
                            TelaEntrar.this.startActivity(trocar);

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),
                                    " não existe login ou senha ",   Toast.LENGTH_LONG).show();

                        }


                    }
                } );



    }
    private void inserir()
    {
        url=Host+"inserirt.php";
        Ion.with (TelaEntrar.this)
                .load ( url )
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setBodyParameter ( "nome_user" ,"Pedro Costa")
                .setBodyParameter ( "email_user" ,"pcp161107@gmail.com")
                .setBodyParameter ( "senha_user","pcp123")
                .asJsonObject ()
                .setCallback ( new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        ret=result.get("status").getAsString ();
                        if(ret.equals ( "ok" ))
                        {
                            Toast.makeText(getApplicationContext(),
                                    " incluido com sucesso", Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),  " erro",
                                    Toast.LENGTH_LONG).show();

                        }


                    }
                } );

    }*/
}
