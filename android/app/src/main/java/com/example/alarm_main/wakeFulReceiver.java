package com.example.alarm_main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class wakeFulReceiver extends BroadcastReceiver {
    String path = "";
    String timeString = "";
    String message = "";
    Boolean customPath = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("In onReceive [wakeFulReceiver.class]");

        Intent i = new Intent();
        i.setClassName("com.example.alarm_main", "com.example.alarm_main.AlarmPage");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

         timeString = intent.getStringExtra("timeString");
         message = intent.getStringExtra("message");
         customPath = intent.getBooleanExtra("customPath", false);

        if (customPath) {
            path = intent.getStringExtra("path");
            i.putExtra("path", path);
            System.out.println("In Receiver  the values " + path + customPath);
        }

        i.putExtra("message", message);
        i.putExtra("timeString", timeString);

        context.startActivity(i);

        Thread thread = new Thread() {
            @Override
            public void run() {
                //  context.startService(new Intent(context, MusicService.class));

                context.startService(new Intent(context, NotificationService.class));
            }
        };
        thread.start();
        Thread threadTwo = new Thread() {
            @Override
            public void run() {
                //  context.startService(new Intent(context, MusicService.class));
                Intent musicServiceIntent = new Intent(context, MusicService.class);
                musicServiceIntent.putExtra("customPath", customPath);
                musicServiceIntent.putExtra("path", path);
                context.startService(musicServiceIntent);
            }
        };
        threadTwo.start();
    }

}