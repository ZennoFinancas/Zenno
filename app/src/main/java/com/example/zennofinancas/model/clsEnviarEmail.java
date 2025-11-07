package com.example.zennofinancas.model;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class clsEnviarEmail {


    // Substitui o titulo e descrição enviados no email padrão
    public static void enviar(Context contexto, String destinatario, String assunto, String tituloEmail, String descricaoEmail, int codigoVerificacao) {

        // Configurações das credenciais SMTP
        final String remetente = "financaszenno@gmail.com";
        final String senha = "hjrq uzyv oonq hrtz";

        // Permite o envio na thread principal (para simplificar)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            // Configurações do servidor SMTP do Gmail
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            // Autenticação com o e-mail remetente
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(remetente, senha);
                }
            });

            String corpoEmail = "<html>" +
                    "<head>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<style>" +
                    "@import url('https://fonts.googleapis.com/css2?family=League+Spartan:wght@400;600&display=swap');" +
                    "body { font-family: 'League Spartan', Arial, sans-serif; margin: 0; padding: 0; background-color: #f2f2f2; }" +
                    ".container { max-width: 420px; margin: auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                    ".header { background-color: #512da8; color: #fff; text-align: center; padding: 24px 16px; }" +  // Roxo
                    ".header h1 { font-size: 22px; margin: 0; font-weight: 600; letter-spacing: 0.5px; }" +
                    ".content { padding: 20px; color: #333333; }" +
                    ".content p { font-size: 15px; line-height: 1.6; margin-bottom: 18px; }" +
                    ".button { display: block; width: fit-content; background-color: #009688; color: white; text-decoration: none; font-weight: 600; padding: 12px 22px; border-radius: 6px; margin: 20px auto; text-align: center; }" + // Verde
                    ".footer { background-color: #512da8; color: #ffffff; text-align: center; padding: 14px; font-size: 13px; font-weight: 500; }" +
                    ".footer-bottom { background-color: #2e2b2b; color: #ffffff; text-align: center; padding: 20px 10px; font-size: 12px; line-height: 1.6; }" +
                    ".footer-bottom a { color: #ffffff; text-decoration: none; }" +
                    "@media only screen and (max-width: 480px) { .container { width: 94%; } .header h1 { font-size: 20px; } }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h1>" + tituloEmail + "</h1>" +  // variável dinâmica para o título
                    "</div>" +

                    "<div class='content'>" +
                    "<p>" + descricaoEmail + "</p>" +  // variável dinâmica para a descrição

                    "<a href='' class='button'>"+ codigoVerificacao + "</a>" +

                    "<p>Se você não é usuario do nosso aplicativo, ignore este e-mail.</p>" +
                    "</div>" +

                    "<div class='footer'>Seu Aliado Financeiro</div>" +

                    "<div class='footer-bottom'>" +
                    "<p><strong>Zenno Finanças</strong></p>" +
                    "<p>Te ajudando a alcançar novos patamares.</p>" +
                    "<p>Rua das Inovações, 245 – Centro, São Bernardo do Campo/SP</p>" +
                    "<p>(11) 3456-7890 | (11) 98765-4321</p>" +
                    "<p><a href='mailto:contato@zennofinancas.com.br'>contato@zennofinancas.com.br</a></p>" +
                    "<p><a href='https://www.zennofinancas.com.br'>www.zennofinancas.com.br</a></p>" +
                    "<p>Atendimento: Segunda a Sexta, das 8h às 18h</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            // Criação da mensagem
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remetente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);
            message.setContent(corpoEmail, "text/html; charset=utf-8");


            // Envio
            Transport.send(message);

            Toast.makeText(contexto, "E-mail enviado com sucesso!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(contexto, "Erro ao enviar e-mail: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
