package com.mohamadamin.fastsearch.free.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.mohamadamin.fastsearch.free.R;

public class NotificationUtils {

    public static void showSubmitDataNotification(Context context) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.submit_data_notification))
                .setProgress(100, 0, true)
                .setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(-44, notificationBuilder.build());

    }

    public static void dismissSubmitDataNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(-44);
    }

}
