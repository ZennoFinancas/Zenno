package com.example.zennofinancas.api;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.zennofinancas.MainActivity;
import com.example.zennofinancas.TelaInicial;
import com.example.zennofinancas.TelaChecarCodigo;
import com.example.zennofinancas.classes.clsDadosUsuario;
import com.example.zennofinancas.model.clsUsuarioModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class clsUsuarioController {

    public static void logar(Context contexto, String email, String senha) {
        clsUsuarioModel.buscarUsuario(contexto, email, senha, (e, result) -> {
            if (e != null) {
                Toast.makeText(contexto, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            if (result != null && result.size() > 0) {
                JsonObject usuario = result.get(0).getAsJsonObject();
                String id = usuario.get("id_usuario").getAsString();
                String nome = usuario.get("nome_usuario").getAsString();

                clsDadosUsuario.salvarUsuario(contexto, nome, id,email);
                Toast.makeText(contexto, "Bem-vindo, " + nome, Toast.LENGTH_LONG).show();

                Intent i = new Intent(contexto, TelaInicial.class);
                i.putExtra("idUsuario", id);
                contexto.startActivity(i);
            } else {
                Toast.makeText(contexto, "Usuário ou senha inválidos.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void cadastrar(Context contexto, String nome, String email, String telefone, String senha) {
        JsonObject json = new JsonObject();
        int codigo = (int) (Math.random() * 10000);

        json.addProperty("nome_usuario", nome);
        json.addProperty("email_usuario", email);
        json.addProperty("numero_usuario", telefone);
        json.addProperty("senha_usuario", senha);
        json.addProperty("codigo_usuario", codigo);

        clsUsuarioModel.inserirUsuario(contexto, json, (e, result) -> {
            if (e != null) {
                Toast.makeText(contexto, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            if (result != null) {
                // Verifica se contém a mensagem de chave duplicada
                if (result.contains("duplicate key value") && result.contains("usuarios_email_key")) {
                    Toast.makeText(contexto, "Este e-mail já está cadastrado. Tente fazer login.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            Intent i = new  Intent(contexto, TelaChecarCodigo.class);

            //Enviando email para exibir na tela de checar codigo
            i.putExtra("emailUsuario", email);

            Toast.makeText(contexto, "Cadastro realizado com sucesso! Verifique o código no seu email!", Toast.LENGTH_LONG).show();
            contexto.startActivity(i);
        });

    }

    public static void verificarEmail(Context contexto, String email) {
        clsUsuarioModel.buscarUsuario(contexto, email, "", (e, result) -> {
            if (e != null) {
                Toast.makeText(contexto, "Erro de conexão: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            if (result != null && result.size() > 0) {
                Intent i = new Intent(contexto, TelaChecarCodigo.class);
                i.putExtra("emailUsuario", email);
                contexto.startActivity(i);
            } else {
                Toast.makeText(contexto, "Email não encontrado.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
