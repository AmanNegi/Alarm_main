package com.example.alarm_main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
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
import java.util.List;

import io.flutter.Log;
import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.view.FlutterMain;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "aster.flutter.app/alarm/aster";

    public static final String STREAM = "com.aster.eventchannelsample/stream1";
    public static final String STREAM2 = "com.aster.eventPermitChannel/stream2";

    boolean storagePermit = false;
    boolean notificationPermit = false;
    String message, timeString;
    Integer hour, minute, uniqueId, repeating;
    String path = "";
    Boolean customPath = false;
    Handler handler = new Handler();
    Functions functions;

    @Override
    protected void onStart() {
        FlutterMain.startInitialization(getApplicationContext());
        FlutterMain.ensureInitializationComplete(getApplicationContext(), null);

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        functions = new Functions(getApplicationContext());
        GeneratedPluginRegistrant.registerWith(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int val = 0;
        if (!preferences.contains("count")) {
            editor.putInt("count", val);
            editor.apply();
        } else {
            val = preferences.getInt("count", 0);
        }
        if (val < 2) {
            val++;
            requestAutoStartupPermit();
            editor.putInt("count", val);
            editor.apply();
        }

        //Event channel 1
        new EventChannel(getFlutterView(), STREAM).setStreamHandler(
                new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object args, EventChannel.EventSink events) {

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.w("randomTag", "adding listener");
                                HashMap<String, String> map = new HashMap<>();
                                map.put("customPath", customPath.toString());
                                map.put("path", path);
                                events.success(map);
                                //your code
                                handler.postDelayed(this, 1000);
                            }
                        }, 1000);

                    }

                    @Override
                    public void onCancel(Object args) {
                        handler.removeCallbacksAndMessages(null);
                        System.out.println("dismissed the receiver");
                    }
                }
        );

        //Event channel 1
        new EventChannel(getFlutterView(), STREAM2).setStreamHandler(
                new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object args, EventChannel.EventSink events) {

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                                        notificationPermit = true;
                                    }
                                }
                                String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                                int res = getApplicationContext().checkCallingOrSelfPermission(permission);
                                if (res == PackageManager.PERMISSION_GRANTED) {
                                    storagePermit = true;
                                }

                                Log.w(" EventChannel2", "adding listener");
                                HashMap<String, Boolean> map = new HashMap<>();
                                map.put("storagePermit", storagePermit);
                                map.put("notificationPermit", notificationPermit);
                                events.success(map);
                                //your code
                                handler.postDelayed(this, 2000);
                            }
                        }, 1000);

                    }

                    @Override
                    public void onCancel(Object args) {
                        handler.removeCallbacksAndMessages(null);
                        System.out.println("dismissed the receiver");
                    }
                }
        );


        //method Channel
        new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
                new MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, Result result) {
                        switch (call.method) {
                            case "deleteAlarm": {
                                System.out.println(" in delete Alarm [MainActivity.java]");
                                Integer _uniqueIdl = call.argument("uniqueId");
                                functions.cancelAlarm(_uniqueIdl);
                                break;
                            }
                            case "setOneShot": {
                                uniqueId = call.argument("uniqueId");
                                hour = call.argument("hour");
                                message = call.argument("message");
                                minute = call.argument("minute");
                                timeString = call.argument("timeString");
                                functions.setAlarm(hour, minute, uniqueId, false, "", timeString, message);
                                System.out.println(" the values in [mainActivity.java] " + (hour) + " " + (minute));
                                break;
                            }
                            case "getMusicPicker": {
                                if (path != null && path.length() > 0) {
                                    result.success(true);
                                }
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + File.separator);
                                intent.setDataAndType(uri, "audio/*");
                                intent.setType("audio/*");
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(intent, 280);
                                result.success(false);
                                break;
                            }
                            case "updateAlarm": {
                                System.out.println(" in update Alarm [MainActivity.java]");
                                repeating = call.argument("repeating");
                                timeString = call.argument("timeString");
                                uniqueId = call.argument("uniqueId");
                                hour = call.argument("hour");
                                Integer receivedCustomPath = call.argument("customPath");
                                Boolean path01;
                                if (receivedCustomPath == null) {
                                    path01 = false;
                                } else {
                                    path01 = receivedCustomPath == 1;

                                }

                                String receivedPath = call.argument("path");
                                message = call.argument("message");
                                minute = call.argument("minute");
                                System.out.println(receivedPath);
                                System.out.println(path01);

                                // functions.cancelAlarm(uniqueId);
                                if (repeating == null) {
                                    repeating = 0;
                                }
                                if (repeating == 1) {
                                    functions.setRepeatingAlarm(hour, minute, uniqueId, path01, receivedPath, timeString, message);
                                } else {
                                    functions.setAlarm(hour, minute, uniqueId, path01, receivedPath, timeString, message);
                                }
                                break;
                            }
                            case "rescheduleAlarms": {
                                functions.getDatabaseAndRescheduleAlarms();
                                break;
                            }
                            case "showToast": {
                                String text = call.argument("string");
                                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case "requestAllPermit": {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    String packageName = getApplicationContext().getPackageName();
                                    PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                                    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                                        Intent intent1 = new Intent();
                                        intent1.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                        intent1.setData(Uri.parse("package:" + packageName));
                                        startActivity(intent1);
                                    }
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1236);

                                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                        startActivity(intent);
                                    } else {
                                        notificationPermit = true;
                                    }

                                }
                                break;
                            }
                        }

                    }

                });
    }


    void requestAutoStartupPermit() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            } else {
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);

            }
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {

                Toast.makeText(this, "Kindly grant the autoStartUp permission ", Toast.LENGTH_LONG).show();
                startActivity(intent);


            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1236) {
            System.out.println("in permissionResult");
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                System.out.println(permission);
                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("in permission denied");
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 1236);
                        Toast.makeText(this, "Kindly grant the permit to read your files", Toast.LENGTH_SHORT).show();

                    } else {
                        storagePermit = true;
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("in ONActivityResult");
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == 280) {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "The file you selected may take a while to reflect", Toast.LENGTH_SHORT).show();
                    Uri uri = data.getData();
                    path = PathUtils.getPath(getApplicationContext(), uri);
                    customPath = path != null;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

