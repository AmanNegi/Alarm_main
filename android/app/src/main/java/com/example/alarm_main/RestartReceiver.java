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
    String colTimeString = "timeString";
    String colIntList = "listInt";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            //Toast.makeText(context, "reStarting services", Toast.LENGTH_SHORT).show();
            String path = context.getFilesDir().getPath();
            SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/user/0/com.example.alarm_main/app_flutter/alarm.db", null, 0);

            ArrayList<String> message = new ArrayList<>();
            ArrayList<Integer> hour = new ArrayList<>();
            ArrayList<Integer> minute = new ArrayList<>();
            ArrayList<Integer> id = new ArrayList<>();
            ArrayList<ArrayList<Integer>> daysList = new ArrayList<>();


            String[] columns = {colId, colHour, colMinute, colMessage, colIntList};
            Cursor cursor = database.query(tableName, columns, null, null, null, null, null);
            cursor.moveToFirst();

            //format of table
            //   || id | hour | minute | message | timeString | listInt ||

            id.add(cursor.getInt(0));
            hour.add(cursor.getInt(1));
            minute.add(cursor.getInt(2));
            message.add(cursor.getString(3));
            byte[] a = cursor.getBlob(5);


            ArrayList<Integer> ad = new ArrayList<>();
            for (byte l : a) {
                ad.add(Integer.valueOf(l));
            }
            daysList.add(ad);


            while (cursor.moveToNext()) {
                id.add(cursor.getInt(0));
                hour.add(cursor.getInt(1));
                minute.add(cursor.getInt(2));
                message.add(cursor.getString(3));
                byte[] io = cursor.getBlob(5);
                ArrayList<Integer> lm = new ArrayList<>();
                for (byte l : io) {
                    lm.add(Integer.valueOf(l));
                }
                daysList.add(lm);
            }
            for (int i = 0; i < hour.size(); i++) {
                int mainHour = hour.get(i);
                int mainMinute = minute.get(i);
                int mainUniqueID = id.get(i);
                String mainMessage = message.get(i);
                ArrayList<HashMap<String, Boolean>> harshList = createHashMap(daysList.get(i));

                Functions functions = new Functions(mainHour, mainMinute, mainUniqueID, mainMessage, context);

                functions.startUpdateProcess(harshList);
            }
        }


    }

    ArrayList<HashMap<String, Boolean>> createHashMap(ArrayList<Integer> integers) {

        ArrayList<HashMap<String, Boolean>> randomList = new ArrayList<>();
        for (int l = 0; l < integers.size(); l++) {
            HashMap<String, Boolean> var = new HashMap<>();
            for (int i = 0; i < 7; i++) {
                switch (i) {
                    case 0:
                        var.put("Sunday", getTrueOrFalse(integers.get(i)));
                        break;
                    case 1:
                        var.put("Monday", getTrueOrFalse(integers.get(i)));
                        break;
                    case 2:
                        var.put("Tuesday", getTrueOrFalse(integers.get(i)));
                        break;
                    case 3:
                        var.put("Wednesday", getTrueOrFalse(integers.get(i)));
                        break;
                    case 4:
                        var.put("Thursday", getTrueOrFalse(integers.get(i)));
                        break;
                    case 5:
                        var.put("Friday", getTrueOrFalse(integers.get(i)));
                        break;
                    case 6:
                        var.put("Saturday", getTrueOrFalse(integers.get(i)));
                        break;
                }
                randomList.add(var);
                var = new HashMap<>();

            }
        }
        return randomList;
    }

    boolean getTrueOrFalse(Integer no) {
        if (no == 1) {
            return true;
        } else {
            return false;
        }
    }
}
