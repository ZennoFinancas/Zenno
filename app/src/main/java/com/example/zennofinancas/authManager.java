package com.example.zennofinancas;
import android.content.Context;
import android.widget.Toast;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class authManager {

    private final authService authService;

    public authManager() {
        authService = apiSupabase.getClient().create(authService.class);
    }

    public void registerUser(String email, String password, Context context) {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("nome_user", email);
        jsonBody.addProperty("senha_user", password);

        Call<JsonObject> call = authService.register(jsonBody);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Erro ao cadastrar: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

