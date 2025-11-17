package com.example.zennofinancas.api;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.zennofinancas.R;

public class clsFirebaseService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "canal_zenno_financas";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (remoteMessage.getNotification() == null) return;

        String titulo = remoteMessage.getNotification().getTitle();
        String mensagem = remoteMessage.getNotification().getBody();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificações Zenno Finanças",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(canal);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this)
                .notify((int) System.currentTimeMillis(), builder.build());
    }
}
