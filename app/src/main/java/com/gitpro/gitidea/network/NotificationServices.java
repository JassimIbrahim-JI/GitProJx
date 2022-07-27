package com.gitpro.gitidea.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.gitpro.gitidea.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationServices extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String title=message.getNotification().getTitle();
        String body=message.getNotification().getBody();
        final String CHANNEL_ID="HEADS-UP-NOTIFICATION";
        NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"User Notification",
                NotificationManager.IMPORTANCE_DEFAULT);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        Notification.Builder notification=new Notification.Builder(this,CHANNEL_ID);
         notification.setContentTitle(title).setContentText(body).
                 setSmallIcon(R.drawable.logos).setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1, notification.build());

    }
}
