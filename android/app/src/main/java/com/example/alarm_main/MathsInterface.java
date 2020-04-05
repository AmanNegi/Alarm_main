package com.example.alarm_main;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.widget.Toast;



import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MathsInterface extends FlutterActivity {

    @Override
    public FlutterEngine provideFlutterEngine(@NonNull Context context) {
        return FlutterEngineClass.getFlutterEngine();
    }

    @Override
    protected void onPause() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "It ain't gonna work", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserLeaveHint() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int uniqueId = getIntent().getIntExtra("uniqueId", 0);
        boolean repeating = getIntent().getBooleanExtra("repeating", false);

        new MethodChannel(getFlutterEngine().getDartExecutor(), "aster.plugins.dev/mathChannel")
                .setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
                if (methodCall.method.equals("CloseAllWindows")) {
                    Toast.makeText(MathsInterface.this, "Dismissed Alarm", Toast.LENGTH_SHORT).show();
                    if (!repeating) {
                        boolean successInDeletion = AlarmHelper.deleteAlarm(uniqueId);
                        System.out.println(" The result of deletion in MathsInterface of id :- " + uniqueId + " is " + successInDeletion);
                    }
                    Intent musicService = new Intent(getApplicationContext(), MusicService.class);
                    stopService(musicService);

                    Intent myService = new Intent(getApplicationContext(), NotificationService.class);
                    stopService(myService);

                    getFlutterEngine().getNavigationChannel().popRoute();
                    finish();
                }
            }
        });

    }


}
