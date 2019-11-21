package com.example.alarm_main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.view.FlutterMain;

public class RestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //called when the device restarts to reschedule the alarms
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
          //  FlutterMain.startInitialization(context);
          //  FlutterMain.ensureInitializationComplete(context, null);

            //FlutterEngine a = new FlutterEngine(context);
           // Intent flutterIntent = FlutterActivity.withNewEngine().build(context);
           //context.startActivity(flutterIntent);

            Functions functions = new Functions(context);
            functions.getDatabaseAndRescheduleAlarms();
        }
    }


}
