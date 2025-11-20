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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class clsMetodos
{
    //Chave API para fazer requisições ao supabase
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtkc3V2bGFlZXB3anpxbmZ2eHhyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTYxNTMwMTIsImV4cCI6MjA3MTcyOTAxMn0.iuOiaoqm3BhPyEMs6mtn2KlA2CIuYdnkcfmc36_Z8t8";

    // Método Realizar Login
    public static void loginUsuario(Context contexto, String emailUsuario, String senhaUsuario)
    {
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/" + "usuarios?email_usuario=eq." + emailUsuario + "&senha_usuario=eq." + senhaUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray() // o Supabase retorna uma array com os registros inseridos
                .setCallback(new FutureCallback<JsonArray>()
                {
                    @Override
                    public void onCompleted(Exception e, JsonArray result)
                    {
                        if (e != null)
                        {
                            e.printStackTrace();
                            Toast.makeText(contexto, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Se a array de resultados não for nula e tiver pelo menos um item, o login foi um sucesso
                        if (result != null && result.size() > 0)
                        {
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
                            editor.putString("emailUsuario", emailUsuario);
                            editor.apply();

                            Toast.makeText(contexto, "Bem-vindo, " + nomeUsuario, Toast.LENGTH_LONG).show();
                            Intent trocar = new Intent(contexto, TelaInicial.class);

                            trocar.putExtra("idUsuario", idUsuario);
                            contexto.startActivity(trocar);

                        }

                        else
                        {

                            Toast.makeText(contexto, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    // Método para cadastrar usuários
    public static void inserirUsuario(Context contexto, String nomeUsuario, String emailUsuario, String telefoneUsuario, String senhaUsuario)
    {
        //Gerando numero de verificação para o usuario.
        int codigoVerificacao = (int) (Math.random() * 10000);

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
                .setCallback(new FutureCallback<String>()
                {
                    @Override
                    public void onCompleted(Exception e, String result)
                    {

                        // BD nao valida se o email já foi cadastrado ou não.
                        if (e != null)
                        {
                            Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (result != null)
                        {
                            // Verifica se contém a mensagem de chave duplicada
                            if (result.contains("duplicate key value") && result.contains("usuarios_email_key"))
                            {
                                Toast.makeText(contexto, "Este e-mail já está cadastrado. Tente fazer login.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        Intent intent = new Intent(contexto, TelaChecarCodigo.class);

                        //Enviando email para exibir na tela de checar codigo
                        intent.putExtra("emailUsuario", emailUsuario);
                        intent.putExtra("controleChecarCod", "cadastrar");
                        intent.putExtra("codigoVerificacao", String.valueOf(codigoVerificacao));

                        Toast.makeText(contexto, "Cadastro realizado com sucesso! Verifique o código no seu email!", Toast.LENGTH_LONG).show();
                        contexto.startActivity(intent);
                    }

                });
    }

    // Método Verificar Email Cadastrado
    public static void verificarEmail(Context contexto, String emailUsuario)
    {
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/" + "usuarios?email_usuario=eq." + emailUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray() // o Supabase retorna uma array com os registros inseridos
                .setCallback(new FutureCallback<JsonArray>()
                {
                    @Override
                    public void onCompleted(Exception e, JsonArray result)
                    {
                        if (e != null)
                        {
                            e.printStackTrace();
                            Toast.makeText(contexto, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Se a array de resultados tiver pelo menos um item, o email foi encontrado
                        if (result != null && result.size() > 0)
                        {

                            Toast.makeText(contexto, "Email encontrado com sucesso! ", Toast.LENGTH_LONG).show();

                            //Pegar os dados do usuario encontrado
                            JsonObject usuario = result.get(0).getAsJsonObject();

                            // Pega o codigo de verifição cadastrado no BD
                            String codigoVerificacao = usuario.get("codigo_usuario").getAsString();

                            Intent i = new Intent(contexto, TelaChecarCodigo.class);
                            i.putExtra("emailUsuario", emailUsuario);
                            i.putExtra("controleChecarCod", "alterar");
                            i.putExtra("codigoVerificacao", codigoVerificacao);
                            contexto.startActivity(i);

                        } else {

                            Toast.makeText(contexto, "Email não encontrado.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



    public static void alterarSenha(Context contexto, String emailUsuario, String novaSenha) {

        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/" + "usuarios?email_usuario=eq." + emailUsuario;

        // Gera novo código aleatório
        int novoCodigo = (int) (Math.random() * 10000);

        // Cria o objeto JSON com a nova senha para o corpo da requisição
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("senha_usuario", novaSenha);
        jsonBody.addProperty("codigo_usuario", novoCodigo);

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

                            Intent i = new Intent(contexto, TelaEntrar.class);
                            contexto.startActivity(i);

                        } else {
                            // Se a requisição foi bem-sucedida, mas nenhum registro foi retornado,
                            // significa que o usuário não foi encontrado ou não houve alteração.
                            Toast.makeText(contexto, "Falha ao alterar senha." + emailUsuario, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    // Método para validar o e-mail do usuário (ativar conta)
    public static void validarEmailUsuario(Context contexto, String emailUsuario) {
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/usuarios?email_usuario=eq." + emailUsuario;

        // Gera novo código aleatório
        int novoCodigo = (int) (Math.random() * 10000);

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("status_usuario", "Ativo");
        jsonBody.addProperty("codigo_usuario", novoCodigo);

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
                        Toast.makeText(contexto, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (result != null && result.size() > 0) {
                        Toast.makeText(contexto, "Conta ativada com sucesso!", Toast.LENGTH_LONG).show();

                        // Redirecionar para tela de login
                        Intent intent = new Intent(contexto, TelaEntrar.class);
                        contexto.startActivity(intent);

                    } else {
                        Toast.makeText(contexto, "Erro ao validar usuário.", Toast.LENGTH_LONG).show();
                    }
                });
    }






     //inserir receita
     public static void inserirReceita(Context contexto, String idUsuario, String idCategoria, String valorReceita, String descricaoReceita, String dataReceita) {


         // Conversões seguras
         int idCategoriaInt;
         float valorReceitaFloat;



         valorReceitaFloat = Float.parseFloat(valorReceita.replace(",", "."));
         // Criação do JSON para envio
         JsonObject json = new JsonObject();
         json.addProperty("id_usuario", idUsuario);
         json.addProperty("id_categoria", idCategoria);
         json.addProperty("valor_receita", valorReceitaFloat);
         json.addProperty("descricao_receita", descricaoReceita);
         json.addProperty("data_receita", dataReceita);


         String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/receitas";

         Ion.with(contexto)
                 .load("POST", url)
                 .addHeader("Authorization", "Bearer " + API_KEY)
                 .addHeader("apikey", API_KEY)
                 .addHeader("Content-Type", "application/json")
                 .setJsonObjectBody(json)
                 .asString()
                 .setCallback(new FutureCallback<String>() {
                     @Override
                     public void onCompleted(Exception e, String result) {

                         if (e != null) {
                             e.printStackTrace();
                             Toast.makeText(contexto, "Erro na conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                             return;
                         }

                         // Verifica se o servidor retornou erro explícito
                         if (result != null && (result.contains("error") || result.contains("status"))) {
                             Toast.makeText(contexto, "Falha ao cadastrar receita: " + result, Toast.LENGTH_LONG).show();
                             return;
                         }

                         // Sucesso
                         Toast.makeText(contexto, "Receita cadastrada com sucesso!", Toast.LENGTH_LONG).show();

                     }
                 });
     }


    public static void buscarCategorias(Context contexto, String idUsuario, String tipo, FutureCallback<ArrayList<String[]>> callback) {
        // Campos corretos conforme tabela: id_categoria, id_usuario, nome, tipo
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/categorias"
                + "?select=id_categoria,nome&id_usuario=eq." + idUsuario + "&tipo=eq." + tipo ; //

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
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
                            String nomeCategoria = item.get("nome").getAsString(); // <--- CORRIGIDO AQUI
                            categorias.add(new String[]{idCategoria, nomeCategoria});
                        }
                    }

                    callback.onCompleted(null, categorias);
                });
    }


    // Carregar Saldo na tela

    public interface SaldoCallback {
        void onResultado(double saldo);
    }

    public static void buscarSaldo(Context contexto, String idUsuario, SaldoCallback callback) {
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/receitas?id_usuario=eq." + idUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray()
                .setCallback((e, result) -> {

                    if (e != null) {
                        e.printStackTrace();
                        Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    double soma = 0.0;

                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject linha = result.get(i).getAsJsonObject();
                            if (linha.has("valor_receita") && !linha.get("valor_receita").isJsonNull()) {
                                soma += linha.get("valor_receita").getAsDouble();
                            }
                        }
                    }

                    // 🔥 devolve o resultado para quem chamou
                    callback.onResultado(soma);
                });
    }

    public interface DespesaCallback {
        void onResultado(double totalDespesas);
    }

    public static void buscarDespesas(Context contexto, String idUsuario, DespesaCallback callback) {
        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/despesas?id_usuario=eq." + idUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray()
                .setCallback((e, result) -> {

                    if (e != null) {
                        e.printStackTrace();
                        Toast.makeText(contexto, "Erro ao buscar despesas: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    double soma = 0.0;

                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            JsonObject item = result.get(i).getAsJsonObject();

                            if (item.has("valor_despesa") && !item.get("valor_despesa").isJsonNull()) {
                                soma += item.get("valor_despesa").getAsDouble();
                            }
                        }
                    }

                    callback.onResultado(soma);
                });
    }

    //Objetivos

    public static void inserirObjetivo(Context contexto,
                                       String idUsuario,
                                       String nomeObjetivo,
                                       String valorDesejado,
                                       String dataObjetivo) {

        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/objetivos";

        // Converter o valor desejado para padrão numérico
        float valor = 0;
        try {
            valor = Float.parseFloat(valorDesejado.replace(",", "."));
        } catch (Exception e) {
            Toast.makeText(contexto, "Valor inválido!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar JSON
        JsonObject json = new JsonObject();
        json.addProperty("id_usuario", idUsuario);
        json.addProperty("nome_objetivo", nomeObjetivo);
        json.addProperty("valor_desejado", valor); // Inserir data

        Ion.with(contexto)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asString()
                .setCallback((e, result) -> {

                    if (e != null) {
                        e.printStackTrace();
                        Toast.makeText(contexto, "Erro ao cadastrar objetivo: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(contexto, "Objetivo cadastrado com sucesso!", Toast.LENGTH_LONG).show();
                });
    }

    public interface MetasCallback {
        void onResultado(Exception e, ArrayList<String[]> metas);
    }


    public static void buscarObjetivos(Context contexto, String idUsuario, MetasCallback callback) {

        String url = "https://kdsuvlaeepwjzqnfvxxr.supabase.co/rest/v1/objetivos?id_usuario=eq." + idUsuario;

        Ion.with(contexto)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .asJsonArray()
                .setCallback((e, result) -> {

                    if (e != null) {
                        callback.onResultado(e, null);
                        return;
                    }

                    if (result == null || result.size() == 0) {
                        callback.onResultado(null, new ArrayList<>());
                        return;
                    }

                    ArrayList<String[]> listaMetas = new ArrayList<>();

                    for (int i = 0; i < result.size(); i++) {
                        JsonObject linha = result.get(i).getAsJsonObject();

                        String idMeta = linha.has("id") && !linha.get("id").isJsonNull()
                                ? linha.get("id").getAsString()
                                : "";

                        String nomeObjetivo = linha.has("nome_objetivo") && !linha.get("nome_objetivo").isJsonNull()
                                ? linha.get("nome_objetivo").getAsString()
                                : "";

                        String valorDesejado = linha.has("valor_desejado") && !linha.get("valor_desejado").isJsonNull()
                                ? linha.get("valor_desejado").getAsString()
                                : "0";

                        // agora retorna id, nome, valor
                        listaMetas.add(new String[]{idMeta, nomeObjetivo, valorDesejado});
                    }

                    callback.onResultado(null, listaMetas);
                });
    }







}
