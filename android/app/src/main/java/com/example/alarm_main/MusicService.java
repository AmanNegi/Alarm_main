package com.example.alarm_main;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

public class MusicService extends Service {
    private Boolean customPath;
    private String path;
    private static MediaPlayer player;
    private static Vibrator vibrator;
    private static AudioClass audioClass = new AudioClass();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        customPath = intent.getBooleanExtra("customPath", false);
        path = intent.getStringExtra("path");
        doWork();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            vibrator.cancel();
            player.stop();
            player.release();
            customPath = false;
            audioClass.setLowVolume();
        } catch (Exception e) {
            System.out.println("Some error while closing the musicService");
        }
    }

    private void doWork() {
        checkWriteExternalPermission();

        if (customPath) {
            Uri uri = Uri.parse(path);
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
            vibrator.vibrate(10000);
        }
        audioClass.initiateAudioManager(this);
        audioClass.increaseVolumeWithTime();
        player.start();
    }


    private void checkWriteExternalPermission() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        }
    }
}
