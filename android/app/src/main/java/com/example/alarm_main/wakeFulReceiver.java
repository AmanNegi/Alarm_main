package com.example.alarm_main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class wakeFulReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("In onReceive [wakeFulReceiver.class]");

        Intent i = new Intent();
        i.setClassName("com.example.alarm_main", "com.example.alarm_main.AlarmPage");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String timeString = intent.getStringExtra("timeString");
        String message = intent.getStringExtra("message");
        boolean customPath = intent.getBooleanExtra("customPath", false);
        if (customPath) {
            String path = intent.getStringExtra("path");
            i.putExtra("path", path);
        }

        i.putExtra("customPath",customPath);
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
                musicServiceIntent.putExtra("customPath", false);
                context.startService(new Intent(context, MusicService.class));
            }
        };
        threadTwo.start();
    }

}