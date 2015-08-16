package com.mohamadamin.fastsearch.free.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.activities.SearchActivity;

public class NotificationUtils {

    public static void showSearchNotification(Context context) {

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SearchActivity.class);
        stackBuilder.addNextIntent(new Intent(context, SearchActivity.class));
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.search_first))
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(-75, notificationBuilder.build());

    }

    public static void showSubmitDataNotification(Context context) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.submit_data_notification))
                .setProgress(100, 0, true)
                .setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(-74, notificationBuilder.build());

    }

    public static void dismissSearchNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(-75);
    }

    public static void dismissSubmitDataNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(-74);
    }

}
