package com.example.hmsapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HMSPushService extends HmsMessageService {
    String TAG = "hmsdemo";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "Message received");

        if (remoteMessage.getData().length() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        Log.i(TAG, "getCollapseKey: " + remoteMessage.getCollapseKey()
                + "\n getData: " + remoteMessage.getData()
                + "\n getFrom: " + remoteMessage.getFrom()
                + "\n getTo: " + remoteMessage.getTo()
                + "\n getMessageId: " + remoteMessage.getMessageId()
                + "\n getSendTime: " + remoteMessage.getSentTime()
                + "\n getMessageType: " + remoteMessage.getMessageType()
                + "\n getTtl: " + remoteMessage.getTtl());

        Log.i(TAG, "getBody: " + remoteMessage.getNotification().getBody()
                + "\n getBodyLocalizationKey: " + remoteMessage.getNotification().getBodyLocalizationKey()
                + "\n getChannelId: " + remoteMessage.getNotification().getChannelId()
                + "\n getTitle: " + remoteMessage.getNotification().getTitle()
                + "\n getTitleLocalizationKey: " + remoteMessage.getNotification().getTitleLocalizationKey()
                + "\n getNotifyId: " + remoteMessage.getNotification().getNotifyId());

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("type", remoteMessage.getDataOfMap().get("type"));
        /*intent.putExtra("user_birthday", remoteMessage.getDataOfMap().get("user_birthday"));
        intent.putExtra("cod_comunicado", remoteMessage.getDataOfMap().get("cod_comunicado"));
        intent.putExtra("cod_area", remoteMessage.getDataOfMap().get("cod_area"));
        intent.putExtra("cod_flujo", remoteMessage.getDataOfMap().get("cod_flujo"));
        intent.putExtra("cod_solicitud", remoteMessage.getDataOfMap().get("cod_solicitud"));
        intent.putExtra("cod_notificacion", remoteMessage.getDataOfMap().get("cod_notificacion"));
        intent.putExtra("cod_saludo", remoteMessage.getDataOfMap().get("cod_saludo"));
        intent.putExtra("cod_documento", remoteMessage.getDataOfMap().get("cod_documento"));
        intent.putExtra("nombre_documento", remoteMessage.getDataOfMap().get("nombre_documento"));
        intent.putExtra("ruta_documento", remoteMessage.getDataOfMap().get("ruta_documento"));*/

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int uniqueId = Integer.parseInt(remoteMessage.getDataOfMap().get("cod_notificacion"));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = remoteMessage.getDataOfMap().get("cod_notificacion");
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(uniqueId, builder.build());

        Log.i(TAG, "Message Notification End");
    }
}
