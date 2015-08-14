package com.mohamadamin.fastsearch.free.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.activities.MainActivity;
import com.mohamadamin.fastsearch.free.activities.SearchActivity;

public class SearchWidget extends AppWidgetProvider {

    final static String ACTION_LAUNCH_MAIN_ACTIVITY = "LaunchMainActivity",
            ACTION_LAUNCH_SEARCH_ACTIVITY = "LaunchSearchActivity";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.search_widget);
        Intent intent = new Intent(context, SearchWidget.class);
        PendingIntent pendingIntent;

        intent.setAction(ACTION_LAUNCH_MAIN_ACTIVITY);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.search_launch, pendingIntent);

        intent.setAction(ACTION_LAUNCH_SEARCH_ACTIVITY);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.search_input, pendingIntent);
        views.setOnClickPendingIntent(R.id.search_search, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onReceive(@NonNull Context context,@NonNull Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction() == null) return;
        if (intent.getAction().equals(ACTION_LAUNCH_MAIN_ACTIVITY)) {
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        } else if (intent.getAction().equals(ACTION_LAUNCH_SEARCH_ACTIVITY)) {
            Intent launchIntent = new Intent(context, SearchActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }
    }
}

