package com.example.alarm_main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.view.FlutterMain;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class RestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //called when the device restarts to reschedule the alarms
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            System.out.println("in onReceive in alarm_main please help here");
            Functions functions = new Functions(context);
            functions.getDatabaseAndRescheduleAlarms();
        }
    }


}
