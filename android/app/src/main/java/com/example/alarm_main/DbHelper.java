package com.example.alarm_main;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;

class DbHelper {
    private Context context;

    final static String tableName = "table_name";
    final static String colId = "id";
    final static String colHour = "hour";
    final static String colMinute = "minute";
    final static String colMessage = "message";
    final static String colTimeString = "timeString";
    final static String colRepeating = "repeating";
    final static String colCustomPath = "customPath";
    final static String colPath = "path";
    final static String colDefaultMethod = "defaultMethod";

    DbHelper(Context context) {
        this.context = context;
    }

     static SQLiteDatabase getDatabase() {
        return SQLiteDatabase.openDatabase("/data/user/0/com.example.alarm_main/app_flutter/alarm.db", null, 0);
    }


    void rescheduleAlarms() {
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
            ArrayList<Boolean> defaultMethod = new ArrayList<>();

            String[] columns = {colId, colHour, colMinute, colMessage, colTimeString, colRepeating, colCustomPath, colPath, colDefaultMethod};
            Cursor cursor = database.query(tableName, columns, null, null, null, null, null);
            //format of table
            //   || 0. id |1. hour |2. minute |3. message |4. timeString |5. repeating |6.customPath |7. Path |8. DefaultMethod

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
                        defaultMethod.add(cursor.getInt(8) == 1);
                    } while (cursor.moveToNext());
                }
            }
            //cancelling all alarms before setting them again
            for (int i = 0; i < id.size(); i++) {
                AlarmHelper.cancelAlarm(i, context);
            }

            for (int i = 0; i < hour.size(); i++) {
                System.out.println(timeString.toString());
                int mainHour = hour.get(i);
                int mainMinute = minute.get(i);
                int mainUniqueID = id.get(i);
                String mainMessage = message.get(i);
                Boolean mainRepeating = repeating.get(i);
                String mainTimeString = timeString.get(i);
                Boolean mainCustomPath = customPath.get(i);
                String mainPath = path.get(i);
                Boolean mainDefaultMethod = defaultMethod.get(i);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, mainHour);
                calendar.set(Calendar.MINUTE, mainMinute);

                AlarmHelper alarmHelper = new AlarmHelper(context, mainUniqueID, mainHour, mainMinute, mainRepeating ? 1 : 0, mainCustomPath, mainPath, mainTimeString, mainMessage, mainDefaultMethod);
                System.out.println(calendar.getTimeInMillis());
                if (mainRepeating) {
                    alarmHelper.setRepeatingAlarm();
                    System.out.println("Repeating " + mainTimeString);
                } else {
                    alarmHelper.setOneShot();
                    System.out.println("OneShot " + mainTimeString);
                }

            }
            assert cursor != null;
            cursor.close();
        }
    }


}
