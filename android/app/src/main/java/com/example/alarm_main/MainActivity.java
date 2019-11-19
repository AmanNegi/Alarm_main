package com.example.alarm_main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.view.FlutterMain;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;


public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "aster.flutter.app/alarm/aster";


    String message, timeString;
    Integer hour, minute, uniqueId,repeating;
    String path;
    Boolean customPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GeneratedPluginRegistrant.registerWith(this);


        new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
                new MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, Result result) {
                        switch (call.method) {
                            case "deleteAlarm": {
                                System.out.println(" in delete Alarm [MainActivity.java]");
                                Functions functions = new Functions(message, getApplicationContext(), timeString);
                                Integer _uniqueIdl = call.argument("uniqueId");
                                functions.cancelAlarm(_uniqueIdl);
                                break;
                            }
                            case "setOneShot": {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1236);
                                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (mNotificationManager.isNotificationPolicyAccessGranted()) {

                                    } else {
                                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                        startActivity(intent);

                                    }
                                }
                                uniqueId = call.argument("uniqueId");
                                hour = call.argument("hour");
                                message = call.argument("message");
                                minute = call.argument("minute");
                                timeString = call.argument("timeString");
                                Functions functions = new Functions(message, getApplicationContext(), timeString);
                                functions.setAlarm(hour, minute, uniqueId,false,"");
                                System.out.println(" the values in [mainActivity.java] " + (hour) + " " + (minute));
                                // result.success(true);
                                break;
                            }
                            case "getMusicPicker": {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + File.separator);
                                intent.setDataAndType(uri, "audio/*");
                                intent.setType("audio/*");
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(intent, 280);
                                System.out.println(uri.getPath());
                                result.success(path);
                            }
                            case "updateAlarm": {
                                System.out.println(" in update Alarm [MainActivity.java]");
                                repeating = call.argument("repeating");
                                timeString = call.argument("timeString");
                                uniqueId = call.argument("uniqueId");
                                hour = call.argument("hour");
                                customPath =call.argument("customPath");
                                if (customPath = true) {
                                    path = call.argument("path");
                                }
                                message = call.argument("message");
                                minute = call.argument("minute");
                                Functions functions = new Functions(message, getApplicationContext(), timeString);
                                // functions.cancelAlarm(uniqueId);
                                if (repeating == 1) {
                                    functions.setRepeatingAlarm(hour, minute, uniqueId,customPath,path);
                                } else {
                                    functions.setAlarm(hour, minute, uniqueId,customPath,path);
                                }

                            }


                        }

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("in ONActivityResult");

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 280:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    String a = PathUtils.getPath(getApplicationContext(), uri);
                    path = a;
                }
                break;
        }
    }
}

