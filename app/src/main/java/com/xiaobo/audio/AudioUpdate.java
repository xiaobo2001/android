package com.xiaobo.audio;

import android.annotation.SuppressLint;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.RemoteViews;


public class AudioUpdate extends Service {
    public AudioManager audioManager;

    public AudioUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreate() {
        ComponentName componentName = new ComponentName(AudioUpdate.this, Audio.class);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.audio);
        AppWidgetManager appWidgetManager1 = AppWidgetManager.getInstance(getApplicationContext());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                String audioInfo = "系统最大音量:" + audioManager.getStreamMaxVolume(1) +
                        " 通话最大音量:" + audioManager.getStreamMaxVolume(0) +
                        " 多媒体最大音量:" + audioManager.getStreamMaxVolume(3) + "\n" + "系统音量:" + audioManager.getStreamVolume(1) +
                        " 通话音量:" + audioManager.getStreamVolume(0) +
                        " 多媒体音量:" + audioManager.getStreamVolume(3);
                remoteViews.setTextViewText(R.id.appwidget_text, audioInfo);
                appWidgetManager1.updateAppWidget(componentName, remoteViews);
            }
        });
        thread.start();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ComponentName componentName = new ComponentName(AudioUpdate.this, Audio.class);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.audio);
        AppWidgetManager appWidgetManager1 = AppWidgetManager.getInstance(getApplicationContext());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                String audioInfo = "系统最大音量:" + audioManager.getStreamMaxVolume(1) +
                        " 通话最大音量:" + audioManager.getStreamMaxVolume(0) +
                        " 多媒体最大音量:" + audioManager.getStreamMaxVolume(3) + "\n" + "系统音量:" + audioManager.getStreamVolume(1) +
                        " 通话音量:" + audioManager.getStreamVolume(0) +
                        " 多媒体音量:" + audioManager.getStreamVolume(3);
                remoteViews.setTextViewText(R.id.appwidget_text, audioInfo);
                appWidgetManager1.updateAppWidget(componentName, remoteViews);
            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}