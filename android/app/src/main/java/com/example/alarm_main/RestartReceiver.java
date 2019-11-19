package com.example.alarm_main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class RestartReceiver extends BroadcastReceiver {

    String tableName = "table_name";
    String colId = "id";
    String colHour = "hour";
    String colMinute = "minute";
    String colMessage = "message";
    String colRepeating = "repeating";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            //Toast.makeText(context, "reStarting services", Toast.LENGTH_SHORT).show();
            SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/user/0/com.example.alarm_main/app_flutter/alarm.db", null, 0);

            if (database != null) {
                ArrayList<String> message = new ArrayList<>();
                ArrayList<Integer> hour = new ArrayList<>();
                ArrayList<Integer> minute = new ArrayList<>();
                ArrayList<Integer> id = new ArrayList<>();
                ArrayList<Boolean> repeating = new ArrayList<>();
                ArrayList<String> timeString = new ArrayList<>();


                String[] columns = {colId, colHour, colMinute, colMessage, colRepeating};
                Cursor cursor = database.query(tableName, columns, null, null, null, null, null);
                cursor.moveToFirst();

                //format of table
                //   || 0. id |1. hour |2. minute |3. message |4. timeString |5. repeating

                id.add(cursor.getInt(0));
                hour.add(cursor.getInt(1));
                minute.add(cursor.getInt(2));
                message.add(cursor.getString(3));
                timeString.add(cursor.getString(4));
                repeating.add(cursor.getInt(5) == 1);


                while (cursor.moveToNext()) {
                    id.add(cursor.getInt(0));
                    hour.add(cursor.getInt(1));
                    minute.add(cursor.getInt(2));
                    message.add(cursor.getString(3));
                    timeString.add(cursor.getString(4));
                    repeating.add(cursor.getInt(5) == 1);

                }
                for (int i = 0; i < hour.size(); i++) {
                    int mainHour = hour.get(i);
                    int mainMinute = minute.get(i);
                    int mainUniqueID = id.get(i);
                    String mainMessage = message.get(i);
                    Boolean mainRepeating = repeating.get(i);
                    String mainTimeString = timeString.get(i);
                    Functions functions = new Functions(mainMessage, context, mainTimeString);

                    if (mainRepeating) {
                        functions.setRepeatingAlarm(mainHour, mainMinute, mainUniqueID,false,"");
                    } else {
                        functions.setAlarm(mainHour, mainMinute, mainUniqueID,false,"");
                    }

                }
                cursor.close();
            }
        }


    }
}
