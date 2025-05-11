package com.example.cashledger.modelClasses;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cashledger.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMsgServices extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("RefreshedToken",token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if(message.getNotification()!=null){
            pushNotification(message.getNotification().getTitle(),
                    message.getNotification().getBody());
        }
    }

    void pushNotification(String title,String message){
        NotificationManager nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        final String CHANNEL_ID="push_notify";
        Intent intent=new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,100,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            String name="Custom_Notification";
            String description="Custom Notification creator";
            int importance=NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);

            if(nm!=null)
                nm.createNotificationChannel(channel);

            notification=new Notification.Builder(this)
                    .setContentTitle(title)
                    .setSubText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setChannelId(CHANNEL_ID)
                    .build();
        }
        else{
            notification=new Notification.Builder(this)
                    .setContentTitle(title)
                    .setSubText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        if(nm!=null)
            nm.notify(1,notification);
    }
}
