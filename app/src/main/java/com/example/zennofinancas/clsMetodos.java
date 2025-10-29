package com.example.zennofinancas;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class clsMetodos {
    //Chave API para fazer requisições ao supabase
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";


    // Método Realizar Login
    public static void Logar(Context contexto, String emailUsuario, String senhaUsuario) {
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/" + "usuarios?email_usuario=eq." + emailUsuario + "&senha_usuario=eq." + senhaUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray() // o Supabase retorna uma array com os registros inseridos
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(contexto, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Se a array de resultados não for nula e tiver pelo menos um item, o login foi um sucesso
                        if (result != null && result.size() > 0) {

                            //Pegar os dados do usuario encontrado
                            JsonObject usuario = result.get(0).getAsJsonObject();

                            // Salvar nome do usuario
                            String idUsuario = usuario.get("id_usuario").getAsString();
                            String nomeUsuario = usuario.get("nome_usuario").getAsString();

                            // Salvar login
                            SharedPreferences prefs = contexto.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("nomeUsuario", nomeUsuario);
                            editor.putString("idUsuario", idUsuario);
                            editor.apply();


                            Toast.makeText(contexto, "Bem-vindo, " + nomeUsuario + idUsuario, Toast.LENGTH_LONG).show();
                            Intent trocar = new Intent(contexto, TelaInicial.class);

                            trocar.putExtra("idUsuario", idUsuario);
                            contexto.startActivity(trocar);

                        } else {

                            Toast.makeText(contexto, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    // Método para cadastrar usuários

    public static void Inserir(Context contexto, String nomeUsuario, String emailUsuario, String telefoneUsuario, String senhaUsuario) {

        //Gerando numero de verificação para o usuario.
        double numeroAleatorio = Math.random() * 10000;
        int codigoVerificacao = (int) numeroAleatorio;


        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/usuarios";

        JsonObject json = new JsonObject();
        json.addProperty("nome_usuario", nomeUsuario);
        json.addProperty("email_usuario", emailUsuario);
        json.addProperty("numero_usuario","(11)98876-2290"); //Alterar campo no front
        json.addProperty("senha_usuario", senhaUsuario);
        json.addProperty("codigo_usuario", codigoVerificacao);


        Ion.with(contexto)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)  // corpo em JSON
                .asString() // Change this line
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        // BD nao valida se o email já foi cadastrado ou não.
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        else
                        {
                            // Se não houver erro de exceção, a requisição foi bem-sucedida
                            // Independentemente da resposta do servidor (vazia ou não)
                            Toast.makeText(contexto, "Cadastro realizado com sucesso! " + codigoVerificacao , Toast.LENGTH_LONG).show();
                            Intent trocar = new Intent(contexto, MainActivity.class);
                            contexto.startActivity(trocar);
                        }

                    }
                });
    }

    // Método Verificar Email Cadastrado
    public static void VerificarEmail(Context contexto, String emailUsuario){
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/" + "usuarios?email_usuario=eq." + emailUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray() // o Supabase retorna uma array com os registros inseridos
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(contexto, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Se a array de resultados não for nula e tiver pelo menos um item, o email foi encontrado
                        if (result != null && result.size() > 0) {

                            Toast.makeText(contexto, "Email encontrado com sucesso! ", Toast.LENGTH_LONG).show();
                            Intent trocar = new Intent(contexto, TelaChecarCodigo.class);
                            trocar.putExtra("emailUsuario", emailUsuario);
                            contexto.startActivity(trocar);

                        } else {

                            Toast.makeText(contexto, "Email não encontrado.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    // Método para enviar email ao usuário
    public static void EnviarEmail(){

    }


    public static void AlterarSenha(Context contexto, String emailUsuario, String novaSenha) {

        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/usuarios?email_usuario=eq." + emailUsuario;

        // Cria o objeto JSON com a nova senha para o corpo da requisição
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("senha_usuario", novaSenha);

        Ion.with(contexto)
                .load("PATCH", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .setJsonObjectBody(jsonBody) // Define o corpo JSON com a nova senha
                .asJsonArray() // O Supabase retorna uma array com os registros atualizados
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(contexto, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Se a array de resultados não for nula e tiver pelo menos um item, a senha foi alterada com sucesso
                        if (result != null && result.size() > 0) {
                            Toast.makeText(contexto, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();

                        } else {
                            // Se a requisição foi bem-sucedida, mas nenhum registro foi retornado,
                            // significa que o usuário não foi encontrado ou não houve alteração.
                            Toast.makeText(contexto, "Falha ao alterar senha.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }





     //inserir receita
    public static void inserirReceita(Context contexto, String idUsuario, String idCategoria, String valorReceita, String descricaoReceita, String dataReceita) {



        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/receitas";

        JsonObject json = new JsonObject();
        json.addProperty("id_usuario", idUsuario);
        json.addProperty("id_categoria", idCategoria);
        json.addProperty("valor_receita", valorReceita); //Alterar campo no front
        json.addProperty("descricao_receita", descricaoReceita);
        json.addProperty("data_receita", dataReceita);


        Ion.with(contexto)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)  // corpo em JSON
                .asString() // Change this line
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        // BD nao valida se o email já foi cadastrado ou não.
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        else
                        {
                            // Se não houver erro de exceção, a requisição foi bem-sucedida
                            // Independentemente da resposta do servidor (vazia ou não)
                            Toast.makeText(contexto, "Cadastro realizado com sucesso! ", Toast.LENGTH_LONG).show();
                            Intent trocar = new Intent(contexto, MainActivity.class);
                            contexto.startActivity(trocar);
                        }

                    }
                });

    }


    public static void buscarCategorias(Context contexto, String idUsuario, FutureCallback<ArrayList<String[]>> callback) {

        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/categorias?id_usuario=eq." + idUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray()
                .setCallback((e, result) -> {

                    if (e != null) {
                        Toast.makeText(contexto, "Erro ao buscar categorias", Toast.LENGTH_SHORT).show();
                        callback.onCompleted(e, null);
                        return;
                    }

                    ArrayList<String[]> categorias = new ArrayList<>();

                    if (result != null && result.size() > 0) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject item = result.get(i).getAsJsonObject();

                            String idCategoria = item.get("id_categoria").getAsString();
                            String nomeCategoria = item.get("nome_categoria").getAsString();

                            // Salvando em matriz (cada item = linha da matriz)
                            categorias.add(new String[]{idCategoria, nomeCategoria});
                        }
                    }

                    callback.onCompleted(null, categorias);
                });
    }

}
