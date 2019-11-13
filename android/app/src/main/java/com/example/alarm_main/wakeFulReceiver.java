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
        int hour = intent.getIntExtra("hour", 0);
        int minute = intent.getIntExtra("minute", 0);
        String message = intent.getStringExtra("message");

        i.putExtra("hour",hour);
        i.putExtra("minute",minute);
        i.putExtra("message",message);

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
                context.startService(new Intent(context, MusicService.class));
            }
        };
        threadTwo.start();


    }

}