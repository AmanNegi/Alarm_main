package com.example.alarm_main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //called when the device restarts to reschedule the alarms
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            System.out.println("in onReceive in alarm_main please help here");
            DbHelper dbHelper = new DbHelper(context);
            dbHelper.rescheduleAlarms();
        }
    }


}
