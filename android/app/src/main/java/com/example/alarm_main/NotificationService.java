package com.example.alarm_main;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.widget.Toast;


public class NotificationService extends Service {

    NotificationManager mNotificationManager;
    int notificationId = 34657432;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("Started the notification Service");

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        System.out.println("In onCreate()  [NotificationService.java]");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification();
        return START_STICKY;
    }


    void createNotification() {
        System.out.println("creating Notification");
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    "1",
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            Intent intent = new Intent(this, AlarmPage.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel1);
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.clock)
                    .setLargeIcon(Icon.createWithResource(this,R.drawable.clock))
                    .setStyle(new Notification.BigTextStyle().bigText("Wake up now!!"))
                    .setContentText("Wake up!")
                    .setContentTitle("Alarm")
                    .setAutoCancel(false)
                    .setShowWhen(true)
                    .setOngoing(true)
                    .setChannelId("1")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build();
            mNotificationManager.notify(notificationId, notification);


        } else {
            Toast.makeText(this, "No notification created", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("In onDestroy [NotificationService.java]");

        mNotificationManager.cancel(notificationId);
    }
}





