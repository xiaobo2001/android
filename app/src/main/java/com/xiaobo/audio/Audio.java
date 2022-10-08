package com.xiaobo.audio;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;


/**
 * Implementation of App Widget functionality.
 */
public class Audio extends AppWidgetProvider {

    static void updateAppWidget(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        AppWidgetManager appWidgetManager1 = AppWidgetManager.getInstance(context);
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.audio);
        new Thread(new Runnable() {
            @Override
            public void run() {
                views.setTextViewText(R.id.appwidget_text, widgetText);
                Intent start = new Intent(context, AudioUpdate.class);
                context.startService(start);
            }
        }).start();
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, @NonNull int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}