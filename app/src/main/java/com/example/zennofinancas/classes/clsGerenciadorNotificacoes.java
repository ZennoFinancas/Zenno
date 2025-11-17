package com.example.zennofinancas.classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.zennofinancas.R;
import com.example.zennofinancas.TelaNotificacoes;

public class clsGerenciadorNotificacoes {

    private static final String CHANNEL_ID = "canal_zenno_financas";

    private static void criarCanal(Context context, boolean som, boolean vibracao) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificações Zenno Finanças",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            if (!som) canal.setSound(null, null);
            canal.enableVibration(vibracao);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(canal);
        }
    }

    public static void enviar(Context context, String titulo, String mensagem) {


        SharedPreferences prefs = context.getSharedPreferences(
                TelaNotificacoes.PREFS_NAME,
                Context.MODE_PRIVATE
        );

        boolean desativarTudo = prefs.getBoolean(TelaNotificacoes.KEY_DESATIVAR_TODAS, false);
        boolean som = prefs.getBoolean(TelaNotificacoes.KEY_SONS, true);
        boolean vibracao = prefs.getBoolean(TelaNotificacoes.KEY_VIBRACOES, true);

        if (desativarTudo) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        criarCanal(context, som, vibracao);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (!som) builder.setSound(null);
        if (!vibracao) builder.setVibrate(new long[]{0});

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build()); // ID único
    }
}
