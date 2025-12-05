package com.example.zennofinancas.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.zennofinancas.TelaChecarCodigo;
import com.example.zennofinancas.TelaEntrar;
import com.example.zennofinancas.TelaInicial;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class clsUsuario {

    // CHAVES DO SUPABASE
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";
    private static final String BASE_URL = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/";

    // --- MÉTODOS ---

    /**
     * 1. LOGIN (Corrigido para baixar a foto)
     */
    public static void loginUsuario(Context contexto, String emailUsuario, String senhaUsuario) {
        String url = BASE_URL + "usuarios?email_usuario=eq." + emailUsuario + "&senha_usuario=eq." + senhaUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            Toast.makeText(contexto, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (result != null && result.size() > 0) {
                            JsonObject usuario = result.get(0).getAsJsonObject();

                            String idUsuario = usuario.get("id_usuario").getAsString();
                            String nomeUsuario = usuario.get("nome_usuario").getAsString();

                            // RECUPERAR A FOTO
                            String fotoRecuperada = "";
                            if (usuario.has("foto_usuario") && !usuario.get("foto_usuario").isJsonNull()) {
                                fotoRecuperada = usuario.get("foto_usuario").getAsString();
                            }

                            // Salva TUDO no celular
                            clsDadosUsuario.salvarUsuario(contexto, nomeUsuario, idUsuario, emailUsuario, fotoRecuperada);

                            Toast.makeText(contexto, "Bem-vindo, " + nomeUsuario, Toast.LENGTH_SHORT).show();

                            Intent trocar = new Intent(contexto, TelaInicial.class);
                            trocar.putExtra("idUsuario", idUsuario);
                            trocar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            contexto.startActivity(trocar);

                        } else {
                            Toast.makeText(contexto, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * 2. INSERIR USUÁRIO (Cadastro)
     */
    public static void inserirUsuario(Context contexto, String nomeUsuario, String emailUsuario,
                                      String telefoneUsuario, String senhaUsuario) {
        int codigoVerificacao = (int) (Math.random() * 10000);
        String url = BASE_URL + "usuarios";

        JsonObject json = new JsonObject();
        json.addProperty("nome_usuario", nomeUsuario);
        json.addProperty("email_usuario", emailUsuario);
        json.addProperty("numero_usuario", telefoneUsuario);
        json.addProperty("senha_usuario", senhaUsuario);
        json.addProperty("codigo_usuario", codigoVerificacao);

        Ion.with(contexto)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (result != null && result.contains("duplicate key value")) {
                        Toast.makeText(contexto, "E-mail já cadastrado.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(contexto, TelaChecarCodigo.class);
                    intent.putExtra("emailUsuario", emailUsuario);
                    intent.putExtra("controleChecarCod", "cadastrar");
                    intent.putExtra("codigoVerificacao", String.valueOf(codigoVerificacao));
                    Toast.makeText(contexto, "Cadastro realizado!", Toast.LENGTH_LONG).show();
                    contexto.startActivity(intent);
                });
    }

    /**
     * 3. ALTERAR DADOS (Perfil e Foto)
     */
    public static void alterarDados(Context contexto, String emailUsuario, String nomeUsuario,
                                    String numeroUsuario, String imgUsuario) {
        String url = BASE_URL + "usuarios?email_usuario=eq." + emailUsuario;

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("nome_usuario", nomeUsuario);
        jsonBody.addProperty("numero_usuario", numeroUsuario);
        jsonBody.addProperty("foto_usuario", imgUsuario); // Envia foto Base64 para coluna TEXT

        Ion.with(contexto)
                .load("PATCH", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .setJsonObjectBody(jsonBody)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(contexto, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();

                    Intent it = new Intent(contexto, TelaInicial.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    contexto.startActivity(it);
                });
    }

    /**
     * 4. VERIFICAR EMAIL (Recuperação de Senha)
     */
    public static void verificarEmail(Context contexto, String emailUsuario) {
        String url = BASE_URL + "usuarios?email_usuario=eq." + emailUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null || result == null || result.size() == 0) {
                        Toast.makeText(contexto, "Email não encontrado.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    JsonObject usuario = result.get(0).getAsJsonObject();
                    String codigo = usuario.get("codigo_usuario").getAsString();

                    Intent i = new Intent(contexto, TelaChecarCodigo.class);
                    i.putExtra("emailUsuario", emailUsuario);
                    i.putExtra("controleChecarCod", "alterar");
                    i.putExtra("codigoVerificacao", codigo);
                    Toast.makeText(contexto, "Email encontrado!", Toast.LENGTH_LONG).show();
                    contexto.startActivity(i);
                });
    }

    /**
     * 5. ALTERAR SENHA
     */
    public static void alterarSenha(Context contexto, String emailUsuario, String novaSenha) {
        String url = BASE_URL + "usuarios?email_usuario=eq." + emailUsuario;

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("senha_usuario", novaSenha);
        jsonBody.addProperty("codigo_usuario", (int) (Math.random() * 10000)); // Reseta o código

        Ion.with(contexto)
                .load("PATCH", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(jsonBody)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Falha ao alterar senha.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(contexto, "Senha alterada!", Toast.LENGTH_LONG).show();
                    contexto.startActivity(new Intent(contexto, TelaEntrar.class));
                });
    }

    /**
     * 6. VALIDAR EMAIL DO USUÁRIO (Ativação de conta)
     */
    public static void validarEmailUsuario(Context contexto, String emailUsuario) {
        String url = BASE_URL + "usuarios?email_usuario=eq." + emailUsuario;

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("status_usuario", "Ativo");
        jsonBody.addProperty("codigo_usuario", (int) (Math.random() * 10000));

        Ion.with(contexto)
                .load("PATCH", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(jsonBody)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(contexto, "Erro ao validar usuário.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(contexto, "Conta ativada!", Toast.LENGTH_LONG).show();
                    contexto.startActivity(new Intent(contexto, TelaEntrar.class));
                });
    }
}