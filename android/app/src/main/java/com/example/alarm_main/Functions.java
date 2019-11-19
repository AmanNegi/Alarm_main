package com.example.alarm_main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.AlarmManagerCompat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

class Functions {

    private String timeString;
    private String message;
    private Context context;
    private Boolean customPath;
    private String path;

    Functions(String message, Context context, String timeString) {
        this.timeString = timeString;
        this.message = message;
        this.context = context;
    }

    private void setOneShot(Context context, int requestCode, long startMillis) {
        print("in setOneShot");
        scheduleAlarm(context, requestCode, false, startMillis, 0);

    }

    private void setPeriodic(Context context, int requestCode,
                             long startMillis) {
        print("in setPeriodic");
        scheduleAlarm(context, requestCode, true, startMillis, AlarmManager.INTERVAL_DAY);


    }

    private void scheduleAlarm(
            Context context,
            int requestCode,
            boolean repeating,
            long startMillis,
            long intervalMillis) {

        // Create an Intent for the alarm and set the desired Dart callback handle.
        Intent alarm = new Intent(context, wakeFulReceiver.class);
        alarm.putExtra("timeString", timeString);
        alarm.putExtra("message", message != null ? message : "It's time to wake up");
        alarm.putExtra("customPath", customPath);
        if(customPath){
            alarm.putExtra("path",path);
        }

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, requestCode, alarm, PendingIntent.FLAG_UPDATE_CURRENT);

        // Use the appropriate clock.
        int clock = AlarmManager.RTC_WAKEUP;

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (repeating) {
            manager.setRepeating(clock, startMillis, intervalMillis, pendingIntent);
        } else {
            AlarmManagerCompat.setExactAndAllowWhileIdle(manager, clock, startMillis, pendingIntent);
        }
        message = "";
        timeString = "";
        path = "";
        customPath = false;
    }

    void setRepeatingAlarm(Integer hour, Integer minute, Integer uniqueID, Boolean customPath, String path) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        this.customPath = customPath;
        if(customPath){
            this.path = path;
        }

        setPeriodic(context, uniqueID, calendar.getTimeInMillis());
    }


    private void print(String value) {
        System.out.println(value);
    }

    void cancelAlarm(Integer id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, wakeFulReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, id, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        print("Cancelled alarm with id = " + id.toString());
    }

    void setAlarm(Integer hour, Integer minute, Integer uniqueID, Boolean customPath, String path) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        //initialising values
         this.customPath = customPath;
         if(customPath){
             this.path = path;
         }

        Date currentTime = new Date();
        Date selectedTime = calendar.getTime();

        if (selectedTime.before(currentTime) || selectedTime.equals(currentTime)) {
            calendar.set(Calendar.DATE, currentTime.getDate() + 1);
        }

        Date finalDateObj = calendar.getTime();

        printDifference(currentTime, finalDateObj, context);
        long startMillis = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        print("[setAlarm()] the time of alarm " + startMillis);

        setOneShot(context, uniqueID, calendar.getTimeInMillis());

    }


    private void printDifference(Date startDate, Date endDate, Context context) {
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
            Toast.makeText(context, "Alarm set for " + elapsedHours + " hour " + +elapsedMinutes + " minute", Toast.LENGTH_SHORT).show();
        } else if (elapsedDays > 0 && elapsedHours > 0) {
            Toast.makeText(context, "Alarm set for " + elapsedDays + " days " + elapsedHours + " hour " + elapsedMinutes + " minute", Toast.LENGTH_SHORT).show();
        } else if (elapsedHours == 0 && elapsedDays == 0 && elapsedMinutes >= 1) {
            Toast.makeText(context, "Alarm set for " + elapsedMinutes + " minute", Toast.LENGTH_SHORT).show();
        } else if (elapsedMinutes < 1) {
            Toast.makeText(context, "Alarm set for less than a minute", Toast.LENGTH_SHORT).show();

        }


    }

}
