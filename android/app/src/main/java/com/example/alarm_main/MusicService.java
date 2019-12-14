package com.example.alarm_main;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    Boolean customPath;
    String path;
    MediaPlayer player;
    Vibrator vibrator;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void checkWriteExternalPermission() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);

            }
        }

    }


    void doWork() {
        checkWriteExternalPermission();
        System.out.println("Started the music Service  customMusic = " + customPath);
        System.out.println("The values in the MusicService.java " + path + customPath);

        if (customPath) {
            Uri uri;
            uri = Uri.parse(path);
            player = MediaPlayer.create(this, uri);
            player.setLooping(true);
        } else {
            player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
            player.setLooping(true);
        }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] pattern = new long[8];
            pattern[0] = 500;
            pattern[1] = 50;
            pattern[2] = 500;
            pattern[3] = 50;
            pattern[4] = 500;
            pattern[5] = 50;
            pattern[6] = 500;
            pattern[7] = 50;
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(10000);
        }

        AudioClass audioClass = new AudioClass();
        audioClass.initiateAudioManager(this);
        audioClass.increaseVolumeWithTime();
        player.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        customPath = intent.getBooleanExtra("customPath", false);
        path = intent.getStringExtra("path");
        System.out.println(path + "   " + customPath);

        doWork();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        vibrator.cancel();
        player.stop();
        player.reset();
        player.release();
        customPath = false;
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(this, wakeFulReceiver.class);
        sendBroadcast(intent);
    }
}
