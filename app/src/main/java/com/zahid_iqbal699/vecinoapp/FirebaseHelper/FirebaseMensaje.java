package com.zahid_iqbal699.vecinoapp.FirebaseHelper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zahid_iqbal699.vecinoapp.Activities.LogInActivity;
import com.zahid_iqbal699.vecinoapp.Activities.MainActivity;
import com.zahid_iqbal699.vecinoapp.Activities.SplashScreen;
import com.zahid_iqbal699.vecinoapp.R;

public class FirebaseMensaje  extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        Log.d("MSG==>", "From: " + message.getFrom());
        if (message.getData().size()>0)
        {
            Log.d("MSG==>", "Message data payload: " + message.getData());
            mostrarNotificacion(message.getData().get("Title"), message.getData().get("Message"));
        }

        if (message.getNotification() != null)
        {
            Log.d("MSG==>", "Message Notification Title: " + message.getNotification().getTitle() + ", Body: " +message.getNotification().getBody());
            mostrarNotificacion(message.getNotification().getTitle(), message.getNotification().getBody());
        }


    }

    private RemoteViews getCustomDesign( String  title, String message){
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notificacion);
        remoteViews.setTextViewText(R.id.txtTitulo, title);
        remoteViews.setTextViewText(R.id.txtMensaje, message);
        remoteViews.setImageViewResource(R.id.imageView, R.drawable.app_icon);
        return remoteViews;
    }

    public void mostrarNotificacion(String title, String message){
        Log.d("MSG==>", "mostrarNotificacion From: " + message);
        Intent intent = new Intent(this, MainActivity.class);
        String channel_id = "web_app_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.ic_baseline_lightbulb_circle_24)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            builder = builder.setContent(getCustomDesign(title,message));

        } else
        {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_baseline_lightbulb_circle_24);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id,"VecinoApp",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());


    }


}
