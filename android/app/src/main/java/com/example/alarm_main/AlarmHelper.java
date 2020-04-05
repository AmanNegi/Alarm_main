package com.example.alarm_main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.AlarmManagerCompat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AlarmHelper {
    private int hour;
    private int minute;
    private Context context;
    private int uniqueId;
    private int repeating;
    private Boolean customPath;
    private String path;
    private String timeString;
    private String message;
    private Boolean defaultMethod;
    private Calendar calendar = Calendar.getInstance();
    private Date currentTime = new Date();

    public AlarmHelper(Context context,
                       int uniqueId,
                       int hour,
                       int minute,
                       int repeating,
                       Boolean customPath,
                       String path,
                       String timeString,
                       String message,
                       Boolean defaultMethod) {
        this.context = context;
        this.hour = hour;
        this.minute = minute;
        this.repeating = repeating;
        this.uniqueId = uniqueId;
        this.customPath = customPath;
        this.path = path;
        this.timeString = timeString;
        this.message = message;
        this.defaultMethod = defaultMethod;
    }


    private void scheduleAlarm(long startMillis) {
        try {
            ComponentName receiver = new ComponentName(context, RestartReceiver.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            System.out.println("Encountered an error AlarmHelper.class");
        }

        Intent alarm = new Intent(context, wakeFulReceiver.class);
        System.out.println(" in Functions.java behind the uniqueId " + uniqueId);
        alarm.putExtra("repeating", repeating);
        alarm.putExtra("uniqueId", uniqueId);
        alarm.putExtra("timeString", timeString);
        alarm.putExtra("message", message != null ? message : "It's time to wake up");
        alarm.putExtra("customPath", customPath);
        alarm.putExtra("defaultMethod", defaultMethod);
        if (customPath) {
            alarm.putExtra("path", path);
        }

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, uniqueId, alarm, PendingIntent.FLAG_UPDATE_CURRENT);

        int clock = AlarmManager.RTC_WAKEUP;

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (repeating == 1) {
            long intervalMillis = 24 * 60 * 60 * 1000;
            manager.setRepeating(clock, startMillis, intervalMillis, pendingIntent);
        } else {
            AlarmManagerCompat.setExactAndAllowWhileIdle(manager, clock, startMillis, pendingIntent);
        }
    }

    void setRepeatingAlarm() {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        scheduleAlarm(calendar.getTimeInMillis());
    }

    void setOneShot() {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        Date selectedTime = calendar.getTime();

        if (selectedTime.before(currentTime) || selectedTime.equals(currentTime)) {
            calendar.set(Calendar.DATE, currentTime.getDate() + 1);
        }
        Date finalDateObj = calendar.getTime();
        printDifference(currentTime, finalDateObj, context);
        scheduleAlarm(calendar.getTimeInMillis());
    }

    static void cancelAlarm(Integer id, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, wakeFulReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, id, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        System.out.println("Cancelled alarm with id = " + id.toString());
    }


    static boolean deleteAlarm(int uniqueId) {
        SQLiteDatabase database = DbHelper.getDatabase();
        return database.delete(DbHelper.tableName, DbHelper.colId + "=?", new String[]{String.valueOf(uniqueId)}) == 1;
    }

    private void printDifference(Date startDate, Date endDate, Context context) {
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
        } else {
            Toast.makeText(context, "Alarm has been set", Toast.LENGTH_SHORT).show();
        }


    }
}
