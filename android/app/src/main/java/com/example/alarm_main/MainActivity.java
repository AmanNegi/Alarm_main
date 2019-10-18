package com.example.alarm_main;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "aster.flutter.app/alarm";
    AlarmManager alarmManager;
    Integer hour, minute, uniqueId;

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
                                Integer _uniqueId = call.argument("uniqueId");
                                Integer _hour = call.argument("hour");
                                Integer _minute = call.argument("minute");
                                setAlarm(_hour, _minute, _uniqueId);

                                break;
                            }
                            case "deleteAlarm": {
                                Integer _uniqueIdl = call.argument("uniqueId");
                                cancelAlarm(_uniqueIdl);

                                break;
                            }
                            case "updateAlarm": {
                                ArrayList<HashMap<String, Boolean>> harshMap = call.argument("listWeek");
                                uniqueId = call.argument("uniqueId");
                                hour = call.argument("hour");
                                minute = call.argument("minute");
                                if (harshMap != null) {
                                    cancelAlarm(uniqueId);
                                    setValues(harshMap);
                                }
                                break;
                            }
                        }
                    }
                });

    }

    void setValues(ArrayList<HashMap<String, Boolean>> value) {

        for (HashMap<String, Boolean> map : value)
            for (Map.Entry<String, Boolean> mapEntry : map.entrySet()) {
                String key = mapEntry.getKey();
                Boolean bool = mapEntry.getValue();
                checkValues(key, bool);
            }
    }

    void checkValues(String string, Boolean bool) {
        if (string != null && bool) {
            System.out.println("String " + string + " and bool " + bool);

            switch (string) {
                case "Monday":
                    setValuesForAddingAlarm(2);
                    break;
                case "Tuesday":
                    setValuesForAddingAlarm(3);
                    break;
                case "Wednesday":
                    setValuesForAddingAlarm(4);
                    break;
                case "Thursday":
                    setValuesForAddingAlarm(5);
                    break;
                case "Friday":
                    setValuesForAddingAlarm(6);
                    break;
                case "Saturday":
                    setValuesForAddingAlarm(7);
                    break;
                case "Sunday":
                    setValuesForAddingAlarm(1);
                    break;
                default:
                    Calendar calendar = Calendar.getInstance();
                    int a = calendar.get(Calendar.DAY_OF_WEEK);
                    setValuesForAddingAlarm(a);
            }
        }
    }


    void setValuesForAddingAlarm(int week) {
        updateAlarm(hour, minute, uniqueId, week);
        System.out.println("Week value selected are : " + week);
    }

    void updateAlarm(Integer hour, Integer minute, Integer uniqueId, Integer week) {

        final Intent broadcastReceiverIntent = new Intent(this, wakeFulReceiver.class);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, uniqueId, broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();

        calendar.set(Calendar.DAY_OF_WEEK, week);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        int dateInt;
        if (week <= dayOfWeek && week != 0) {
            dateInt = calendar.getTime().getDate() + 7;

        } else {
            dateInt = calendar.getTime().getDate();
        }

        calendar.set(Calendar.DATE, dateInt);

// TODO check if the alarm works for the repeating alarm....
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        System.out.println("Updated alarm with id  = " + uniqueId.toString() + " and the rest data " + calendar.getTime());
    }

    void cancelAlarm(Integer id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), wakeFulReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), id, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        System.out.println("Cancelled alarm with id = " + id.toString());
    }

    @TargetApi(Build.VERSION_CODES.M)
    void setAlarm(Integer hour, Integer minute, Integer uniqueId) {
        final Intent broadcastReceiverIntent = new Intent(this, wakeFulReceiver.class);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, uniqueId, broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Date currentTime = new Date();
        Date selectedTime = calendar.getTime();

        if (selectedTime.before(currentTime) || selectedTime.equals(currentTime)) {
            calendar.set(Calendar.DATE, currentTime.getDate() + 1);
        }

        Date finalDateObj = calendar.getTime();

        printDifference(currentTime, finalDateObj);
        // System.out.println("time from calendar" + calendar.getTimeInMillis());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1 * 60 * 60 * 1000, pendingIntent);

    }

    //1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400
    public void printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        if (elapsedDays == 0 && elapsedHours > 0) {
            Toast.makeText(this, "Alarm set for " + elapsedHours + " hour " + +elapsedMinutes + " minute", Toast.LENGTH_SHORT).show();
        } else if (elapsedDays > 0 && elapsedHours > 0) {
            Toast.makeText(this, "Alarm set for " + elapsedDays + " days " + elapsedHours + " hour " + elapsedMinutes + " minute", Toast.LENGTH_SHORT).show();
        } else if (elapsedHours == 0 && elapsedDays == 0) {
            Toast.makeText(this, "Alarm set for " + elapsedMinutes + " minute", Toast.LENGTH_SHORT).show();
        } else if (elapsedMinutes < 1) {
            Toast.makeText(this, "Alarm set for less than an minute", Toast.LENGTH_SHORT).show();

        }


    }
}

