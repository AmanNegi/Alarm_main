package com.example.alarm_main;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;


public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "aster.flutter.app/alarm";


    String message;
    Integer hour, minute, uniqueId;

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "Kindly grant the DnD permission to ensure that the alarm rings", Toast.LENGTH_SHORT).show();
                    Intent intent = new
                            Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }

                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GeneratedPluginRegistrant.registerWith(this);


        new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
                new MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, Result result) {
                        switch (call.method) {
                            case "addAlarm": {
                                uniqueId = call.argument("uniqueId");
                                hour = call.argument("hour");
                                message = call.argument("message");
                                minute = call.argument("minute");
                                Functions functions = new Functions(hour, minute, uniqueId, message, getApplicationContext());
                                functions.setAlarm(hour, minute, uniqueId);
                                System.out.println(" the values in [mainActivity.java] " + (hour) + " " + (minute));
                                result.success(true);
                                break;

                            }
                            case "deleteAlarm": {
                                Functions functions = new Functions(hour, minute, uniqueId, message, getApplicationContext());
                                Integer _uniqueIdl = call.argument("uniqueId");
                                functions.cancelAlarm(_uniqueIdl);

                                break;
                            }
                            case "updateAlarm": {
                                ArrayList<HashMap<String, Boolean>> harshMap = call.argument("listWeek");
                                uniqueId = call.argument("uniqueId");
                                hour = call.argument("hour");
                                message = call.argument("message");
                                minute = call.argument("minute");

                                Functions functions = new Functions(hour, minute, uniqueId, message, getApplicationContext());

                                if (harshMap != null) {
                                    functions.cancelAlarm(uniqueId);
                                    functions.startUpdateProcess(harshMap);
                                } else {
                                    System.out.println(" the hashMap is null [MainActivity.java] ");
                                }
                                break;
                            }
                            case "getPermission": {
                                NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (!n.isNotificationPolicyAccessGranted()) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 1);
                                    } else {
                                        result.success(n.isNotificationPolicyAccessGranted());
                                    }
                                }

                            }
                        }

                    }
                });
    }
}
