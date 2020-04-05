package com.example.alarm_main;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
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


public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "aster.flutter.app/alarm/aster";

    public static final String STREAM = "com.aster.eventchannelsample/stream1";
    public static final String STREAM2 = "com.aster.eventPermitChannel/stream2";

    boolean storagePermit = false;
    boolean notificationPermit = false;
    boolean defaultMethod = true;
    String message, timeString;
    Integer repeating = 0;
    Integer hour, minute, uniqueId;
    String path = "";
    Boolean customPath = false;
    Handler handler = new Handler();
    DbHelper dbHelper;

    @Override
    protected void onStart() {
        super.onStart();
        FlutterEngineClass.createFlutterEngine(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        dbHelper = new DbHelper(getApplicationContext());

        saveOpenedCountToSharedPrefs();

        //Event channel 1 --| music stream
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

        //Event channel 1 --| Permission stream
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

        //method Channel --| To get the calls from the flutter code
        new MethodChannel(getFlutterView().getDartExecutor(), CHANNEL)
                .setMethodCallHandler(
                        new MethodCallHandler() {
                            @Override
                            public void onMethodCall(MethodCall call, Result result) {
                                switch (call.method) {
                                    case "deleteAlarm": {
                                        System.out.println(" in delete Alarm [MainActivity.java]");
                                        Integer _uniqueIdl = call.argument("uniqueId");
                                        AlarmHelper.cancelAlarm(_uniqueIdl, getApplicationContext());
                                        break;
                                    }
                                    case "setOneShot": {
                                        uniqueId = call.argument("uniqueId");
                                        hour = call.argument("hour");
                                        message = call.argument("message");
                                        minute = call.argument("minute");
                                        timeString = call.argument("timeString");
                                        AlarmHelper alarmHelper = new AlarmHelper(getApplicationContext(), uniqueId, hour, minute, repeating, customPath, path, timeString, message, defaultMethod);
                                        alarmHelper.setOneShot();
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
                                        defaultMethod = call.argument("defaultMethod");
                                        path = call.argument("path");
                                        message = call.argument("message");
                                        minute = call.argument("minute");
                                        int integerCustomPath = call.argument("customPath");
                                        if (integerCustomPath == 0) {
                                            customPath = false;
                                        } else {
                                            customPath = true;
                                        }
                                        AlarmHelper.cancelAlarm(uniqueId, getApplicationContext());
                                        AlarmHelper alarmHelper = new AlarmHelper(getApplicationContext(), uniqueId, hour, minute, repeating, customPath, path, timeString, message, defaultMethod);

                                        if (repeating == 1) {
                                            alarmHelper.setRepeatingAlarm();
                                        } else {
                                            alarmHelper.setOneShot();
                                        }
                                        break;
                                    }
                                    case "rescheduleAlarms": {
                                        dbHelper.rescheduleAlarms();
                                        break;
                                    }
                                    case "showToast": {
                                        String text = call.argument("string");
                                        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    case "requestAllPermit": {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            String packageName = MainActivity.this.getApplicationContext().getPackageName();
                                            PowerManager pm = (PowerManager) MainActivity.this.getApplicationContext().getSystemService(Context.POWER_SERVICE);
                                            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                                                Intent intent1 = new Intent();
                                                intent1.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                                intent1.setData(Uri.parse("package:" + packageName));
                                                MainActivity.this.startActivity(intent1);
                                            }
                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1236);

                                            NotificationManager mNotificationManager = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                                                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                                MainActivity.this.startActivity(intent);
                                            } else {
                                                notificationPermit = true;
                                            }

                                        }
                                        break;
                                    }

                                }

                            }
                        }
                );
    }

    private void saveOpenedCountToSharedPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int val = 0;
        if (!preferences.contains("count")) {
            editor.putInt("count", val);
            editor.apply();
        } else {
            val = preferences.getInt("count", 0);
        }
        if (val == 0) {
            val++;
            requestAutoStartupPermit();
            editor.putInt("count", val);
            editor.apply();
        }
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

