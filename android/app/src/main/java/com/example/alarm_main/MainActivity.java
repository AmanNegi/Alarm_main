package com.example.alarm_main;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "aster.flutter.app/alarm";
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
                new MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, Result result) {
                        if (call.method.equals("randomMethod")) {
                            Integer uniqueId = call.argument("uniqueId");

                            Integer hour = call.argument("hour");
                            Integer minute = call.argument("minute");

                            setAlarm(hour, minute, uniqueId);

                        }
                    }
                });

    }

    @TargetApi(Build.VERSION_CODES.M)
    void setAlarm(Integer hour, Integer minute,Integer uniqueId) {
        final Intent broadcastReceiverIntent = new Intent(this, wakeFulReceiver.class);

        // TODO create specific id's for each pending intent
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
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

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

        if (elapsedDays == 0 && elapsedHours>0) {
            Toast.makeText(this, "Alarm set for " + elapsedHours + " hour " + +elapsedMinutes + " minute", Toast.LENGTH_SHORT).show();
        } else if (elapsedDays > 0 && elapsedHours>0) {
            Toast.makeText(this, "Alarm set for " + elapsedDays + " days " + elapsedHours + " hour " + elapsedMinutes + " minute", Toast.LENGTH_SHORT).show();
        } else if (elapsedHours == 0 && elapsedDays ==0) {
            Toast.makeText(this, "Alarm set for " +elapsedMinutes +" minute"  , Toast.LENGTH_SHORT).show();
        } else if (elapsedMinutes < 1) {
            Toast.makeText(this, "Alarm set for less than an minute", Toast.LENGTH_SHORT).show();

        }


    }
}

