package com.example.alarm_main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.AlarmManagerCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class Functions {

    private Context context;
    String tableName = "table_name";

    String colId = "id";
    String colHour = "hour";
    String colMinute = "minute";
    String colMessage = "message";
    String colTimeString = "timeString";
    String colRepeating = "repeating";
    String colCustomPath = "customPath";
    String colPath = "path";

    Functions(Context context) {

        this.context = context;
    }

    private void setOneShot(int requestCode, long startMillis, Boolean customPath, String path, String timeString, String message) {
        print("in setOneShot");
        scheduleAlarm(context, requestCode, false, startMillis, 0, customPath, path, timeString, message);

    }

    public SQLiteDatabase getDatabase() {
        return SQLiteDatabase.openDatabase("/data/user/0/com.example.alarm_main/app_flutter/alarm.db", null, 0);
    }

    public boolean deleteAlarm(int uniqueId) {
        SQLiteDatabase database = getDatabase();

        return database.delete(tableName, colId + "=?", new String[]{String.valueOf(uniqueId)}) > 0;
    }

    public void getDatabaseAndRescheduleAlarms() {

        SQLiteDatabase database = getDatabase();
        if (database != null) {
            ArrayList<String> message = new ArrayList<>();
            ArrayList<Integer> hour = new ArrayList<>();
            ArrayList<Integer> minute = new ArrayList<>();
            ArrayList<Integer> id = new ArrayList<>();
            ArrayList<Boolean> repeating = new ArrayList<>();
            ArrayList<String> timeString = new ArrayList<>();
            ArrayList<Boolean> customPath = new ArrayList<>();
            ArrayList<String> path = new ArrayList<>();

            String[] columns = {colId, colHour, colMinute, colMessage, colTimeString, colRepeating, colCustomPath, colPath};
            Cursor cursor = database.query(tableName, columns, null, null, null, null, null);
            //format of table
            //   || 0. id |1. hour |2. minute |3. message |4. timeString |5. repeating |6.customPath |7. Path

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        id.add(cursor.getInt(0));
                        hour.add(cursor.getInt(1));
                        minute.add(cursor.getInt(2));
                        message.add(cursor.getString(3));
                        timeString.add(cursor.getString(4));
                        repeating.add(cursor.getInt(5) == 1);
                        customPath.add(cursor.getInt(6) == 1);
                        path.add(cursor.getString(7));
                    } while (cursor.moveToNext());
                }
            }
            //cancelling all alarms before setting them again
            for (int i = 0; i < id.size(); i++) {
                cancelAlarm(i);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < hour.size(); i++) {
                print(timeString.toString());
                int mainHour = hour.get(i);
                int mainMinute = minute.get(i);
                int mainUniqueID = id.get(i);
                String mainMessage = message.get(i);
                Boolean mainRepeating = repeating.get(i);
                String mainTimeString = timeString.get(i);
                Boolean mainCustomPath = customPath.get(i);
                String mainPath = path.get(i);

                //  functions.cancelAlarm(mainUniqueID);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, mainHour);
                calendar.set(Calendar.MINUTE, mainMinute);

                print(String.valueOf(calendar.getTimeInMillis()));
                if (mainRepeating) {
                    setRepeatingAlarm(mainHour, mainMinute, mainUniqueID, mainCustomPath, mainPath, mainTimeString, mainMessage);
                    print("Repeating " + mainTimeString);
                } else {
                    setAlarm(mainHour, mainMinute, mainUniqueID, mainCustomPath, mainPath, mainTimeString, mainMessage);
                    print("OneShot " + mainTimeString);
                }

            }
            cursor.close();
        }
    }

    private void setPeriodic(Context context, int requestCode,
                             long startMillis, Boolean customPath, String path, String timeString, String message) {
        print("in setPeriodic");
        scheduleAlarm(context, requestCode, true, startMillis, AlarmManager.INTERVAL_DAY, customPath, path, timeString, message);

    }

    private void scheduleAlarm(
            Context context,
            int requestCode,
            boolean repeating,
            long startMillis,
            long intervalMillis,
            Boolean customPath,
            String path, String timeString, String message) {

        ComponentName receiver = new ComponentName(context, RestartReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        // Create an Intent for the alarm and set the desired Dart callback handle.
        Intent alarm = new Intent(context, wakeFulReceiver.class);

        System.out.println(" in Functions.java behind the uniqueId " + String.valueOf(requestCode));
        alarm.putExtra("repeating",repeating);
        alarm.putExtra("uniqueId", requestCode);
        alarm.putExtra("timeString", timeString);
        alarm.putExtra("message", message != null ? message : "It's time to wake up");
        alarm.putExtra("customPath", customPath);
        if (customPath) {
            alarm.putExtra("path", path);
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
    }

    void setRepeatingAlarm(Integer hour, Integer minute, Integer uniqueID, Boolean
            customPath, String path, String timeString, String message) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);


        setPeriodic(context, uniqueID, calendar.getTimeInMillis(), customPath, path, timeString, message);
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

    void setAlarm(Integer hour, Integer minute, Integer uniqueID, Boolean customPath, String
            path, String timeString, String message) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        print("the values in setAlarm() Functions.java " + customPath + "  " + path);
        Date currentTime = new Date();
        Date selectedTime = calendar.getTime();

        if (selectedTime.before(currentTime) || selectedTime.equals(currentTime)) {
            calendar.set(Calendar.DATE, currentTime.getDate() + 1);
        }
        print("The date selected in setAlarm () " + calendar.getTime());

        Date finalDateObj = calendar.getTime();

        printDifference(currentTime, finalDateObj, context);

        setOneShot(uniqueID, calendar.getTimeInMillis(), customPath, path, timeString, message);

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
        } else {
            Toast.makeText(context, "Alarm has been set", Toast.LENGTH_SHORT).show();
        }


    }

}
