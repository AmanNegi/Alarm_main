package com.example.alarm_main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;

public class Functions {

    private Integer trueCount = 0;
    private Integer hour;
    private Integer minute;
    private Integer uniqueID;
    private String message;
    private Context context;

    Functions(Integer hour, Integer minute, Integer uniqueID, String message, Context context) {
        this.hour = hour;
        this.minute = minute;
        this.message = message;
        this.uniqueID = uniqueID;
        this.context = context;
    }


    void startUpdateProcess(ArrayList<HashMap<String, Boolean>> value) {
        System.out.println(" in startUpdateProcess()  [MainActivity.java] " + value.get(0));
        for (HashMap<String, Boolean> map : value) {
            for (Map.Entry<String, Boolean> mapEntry : map.entrySet()) {
                String key = mapEntry.getKey();
                Boolean bool = mapEntry.getValue();
                checkValues(key, bool);
            }
        }
    }

    void checkValues(String string, Boolean bool) {
        if (bool) {
            trueCount++;
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
            }
        } else if (trueCount == 0) {
            setValuesForAddingAlarm(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
            trueCount++;

        }
    }


    void setValuesForAddingAlarm(int week) {
        updateAlarm(hour, minute, uniqueID, week);
        System.out.println(" [updateAlarmProcess()] Week value selected are : " + week);
    }

    void updateAlarm(Integer hour, Integer minute, Integer uniqueId, Integer week) {
        AlarmManager alarmManager;

        final Intent broadcastReceiverIntent = new Intent(context, wakeFulReceiver.class);
        broadcastReceiverIntent.putExtra("hour", hour);
        broadcastReceiverIntent.putExtra("minute", minute);
        broadcastReceiverIntent.putExtra("message", message != null ? message : "It's time to wake up");

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueId, broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, week);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        int dateInt;
        if (week <= dayOfWeek && week != 0 && calendar.getTime().before(cal.getTime())) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            dateInt = calendar.getTime().getDate();
        } else {
            dateInt = calendar.getTime().getDate();
        }

        calendar.set(Calendar.DATE, dateInt);

        int hourMain = getNoOfDays(calendar);

        long difference;
        if (calendar.after(cal)) {
            difference = calendar.getTimeInMillis() - cal.getTimeInMillis();
            print(" difference is :-  " + difference);
            if (hourMain == 0) {
                if (SDK_INT >= 21) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            } else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), hourMain * 24 * 60 * 60 * 1000, pendingIntent);
            }
        }
        System.out.println("[ updateAlarm() ]  Updated alarm with id  = " + uniqueId.toString() + " and the rest data " + calendar.getTime());
    }

    int getNoOfDays(Calendar calendar) {

        int days = 0;

        Calendar calInstance = Calendar.getInstance();

        if (calendar.after(calInstance)) {
            Date one = calendar.getTime();
            Date two = calInstance.getTime();

            int date = one.getDate() - two.getDate();

            if (date <= 7) {

                days = date;

            }
        }
        print(" days value = " + days);
        return days;

    }

    void print(String value) {
        System.out.println(value);
    }

    void cancelAlarm(Integer id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, wakeFulReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, id, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        System.out.println("Cancelled alarm with id = " + id.toString());
    }

    void setAlarm(Integer hour, Integer minute, Integer uniqueId) {
        AlarmManager alarmManager;

        final Intent broadcastReceiverIntent = new Intent(context, wakeFulReceiver.class);
        broadcastReceiverIntent.putExtra("hour", hour);
        broadcastReceiverIntent.putExtra("minute", minute);
        broadcastReceiverIntent.putExtra("message", message != null ? message : "It's time to wake up");

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueId, broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


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

        printDifference(currentTime, finalDateObj, context);
        System.out.println("[setAlarm()] the time of alarm " + calendar.getTimeInMillis());
        if (SDK_INT >= KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

    }

    public void printDifference(Date startDate, Date endDate, Context context) {
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
