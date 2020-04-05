package com.example.alarm_main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends Service {

    private NotificationManager mNotificationManager;
    private final static int NOTIFICATION_ID = 34657432;
    private final static int REQUEST_CODE = 9023;
    private final static String CHANNEL_ID = "9087";
    boolean defaultMethod = true;
    int uniqueId;
    String timeString;
    String message;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        defaultMethod = intent.getExtras().getBoolean("defaultMethod");
        timeString = intent.getExtras().getString("timeString");
        message = intent.getExtras().getString("message");
        uniqueId = intent.getExtras().getInt("uniqueId", 0);
        System.out.println(" NotificationService.java values received : " + defaultMethod);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotification();
        return START_STICKY;
    }

    public void createNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Channel",
                    NotificationManager.IMPORTANCE_HIGH

            );
            channel1.setDescription("Channel for alarm notification");
            mNotificationManager.createNotificationChannel(channel1);

            Intent intent = new Intent(this, AlarmPage.class);
            if (AlarmPage.isOpened) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            intent.putExtra("defaultMethod", defaultMethod);
            intent.putExtra("uniqueId", uniqueId);
            intent.putExtra("timeString",timeString);
            intent.putExtra("message",message);

            System.out.println(" In Notification.java the value " +
                    defaultMethod + " " + uniqueId);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle("Wake Up ! ")
                            .setAutoCancel(false)
                            .setSmallIcon(R.mipmap.alarm_img_foreground)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setShowWhen(true)
                            .setContentIntent(pendingIntent);

            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
             } else {
            System.out.println("No notification created");
           }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("In onDestroy [NotificationService.java]");
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}





