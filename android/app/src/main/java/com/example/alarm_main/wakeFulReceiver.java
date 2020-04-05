package com.example.alarm_main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class wakeFulReceiver extends BroadcastReceiver {
    boolean repeating;
    String path = "";
    String timeString = "";
    String message = "";
    Boolean customPath = false;
    Boolean defaultMethod = true;
    int uniqueId = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("In onReceive [wakeFulReceiver.class]");

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClass(context, AlarmPage.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        uniqueId = intent.getIntExtra("uniqueId", 0);
        timeString = intent.getStringExtra("timeString");
        message = intent.getStringExtra("message");
        customPath = intent.getBooleanExtra("customPath", false);
        repeating = intent.getIntExtra("repeating", 0) == 1;
        defaultMethod = intent.getBooleanExtra("defaultMethod", true);

        System.out.println("Default Method and unique id in WakeFulReceiver : "
                + defaultMethod + " " + uniqueId);

        if (customPath) {
            path = intent.getStringExtra("path");
            i.putExtra("path", path);
            System.out.println("In Receiver  the values " + path + customPath);
        }
        i.putExtra("defaultMethod", defaultMethod);
        i.putExtra("repeating", repeating);
        i.putExtra("message", message);
        i.putExtra("timeString", timeString);
        i.putExtra("uniqueId", uniqueId);

        context.startActivity(i);

        Thread thread = new Thread() {
            @Override
            public void run() {
                Intent notification = new Intent(context, NotificationService.class);
                notification.putExtra("defaultMethod", defaultMethod);
                notification.putExtra("uniqueId", uniqueId);
                notification.putExtra("message", message);
                notification.putExtra("timeString", timeString);
                context.startService(notification);
            }
        };

        Thread threadTwo = new Thread() {
            @Override
            public void run() {
                Intent musicServiceIntent = new Intent(context, MusicService.class);
                musicServiceIntent.putExtra("customPath", customPath);
                musicServiceIntent.putExtra("path", path);
                context.startService(musicServiceIntent);
            }
        };
        thread.start();
        threadTwo.start();
    }

}