package com.example.alarm_main;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MathsInterface extends FlutterActivity {

    @Nullable
    @Override
    public FlutterEngine provideFlutterEngine(@NonNull Context context) {
        FlutterEngine engine = new FlutterEngine(context);
        engine.getDartExecutor().executeDartEntrypoint(new DartExecutor.DartEntrypoint(null, "startMathsCorner"));
        return engine;
    }
    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                +WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int uniqueId = getIntent().getIntExtra("uniqueId", 0);
        boolean repeating = getIntent().getBooleanExtra("repeating", false);

        if (!repeating) {
            Functions functions = new Functions(this);
            boolean result = functions.deleteAlarm(uniqueId);
            System.out.println(" value of deletion in MathsInterface.java  " + result);
        }

        System.out.println(" in MathsInterface.java behind the uniqueId " + String.valueOf(uniqueId));


        new MethodChannel(getFlutterEngine().getDartExecutor(), "aster.plugins.dev/mathChannel").setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                if (methodCall.method.equals("CloseAllWindows")) {

                    Toast.makeText(MathsInterface.this, "anotherClass andorid", Toast.LENGTH_SHORT).show();
                    Intent musicService = new Intent(getApplicationContext(), MusicService.class);
                    stopService(musicService);

                    Intent myService = new Intent(getApplicationContext(), NotificationService.class);
                    stopService(myService);

                    getFlutterEngine().getNavigationChannel().popRoute();

                }
            }
        });

    }


}
