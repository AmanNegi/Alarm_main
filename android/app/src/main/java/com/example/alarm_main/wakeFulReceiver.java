package com.example.alarm_main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class wakeFulReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("In onReceive [wakeFulReceiver.class]");

        Thread thread = new Thread() {
            @Override
            public void run() {
                context.startService(new Intent(context, MusicService.class));
                context.startService(new Intent(context,NotificationService.class));
            }
        };
        thread.start();

        boolean value = AlarmPage.isRunning;
        if(!value){
            Intent i = new Intent();
            i.setClassName("com.example.alarm_main", "com.example.alarm_main.AlarmPage");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else{
            System.out.println("The activity is already running");
        }


    }

}