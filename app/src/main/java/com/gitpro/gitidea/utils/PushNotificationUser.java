package com.gitpro.gitidea.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.ui.ExploreActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;


public class PushNotificationUser extends FirebaseMessagingService {

    private static final String TAG = "NotificationUser";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mUser;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d(TAG, "From: " + message.getFrom());

        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());
        }

         showMyNotification(message);
    }

    public void showMyNotification(RemoteMessage message){
        if(message.getNotification()!=null) {

           mUser= FirebaseAuth.getInstance().getCurrentUser();
            String header = message.getNotification().getTitle();
            String body = message.getNotification().getBody();
            String image= message.getNotification().getIcon();
            final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";

            Intent intent = new Intent(this, ExploreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID);
            notification.setAutoCancel(true);

            int iconApp= R.drawable.app_logo;

            notification.setSmallIcon(iconApp);
            notification.setContentTitle(header);
            notification.setContentText(body);
            notification.setContentIntent(pendingIntent);


            Map<String,Object> adds=new HashMap<>();
            adds.put("title",header);
            adds.put("body",body);
            adds.put("icon",String.valueOf(iconApp));
            db.collection("users/"+mUser.getUid()+"/tokens")
                    .document(message.getMessageId())
                    .set(adds);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Heads Up Notification",
                        NotificationManager.IMPORTANCE_HIGH);
                getSystemService(NotificationManager.class).createNotificationChannel(channel);
            }

            NotificationManagerCompat.from(this).notify(0, notification.build());
        }
    }



    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }


}
