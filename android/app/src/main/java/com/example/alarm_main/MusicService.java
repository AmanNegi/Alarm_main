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

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    boolean customPath;
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

    public void increaseVolumeWithTime(int maxVol) {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            int Vol = 5;

            @Override
            public void run() {
                turnAudioON(Vol);
                if (!(Vol >= maxVol - 2)) {
                    Vol++;
                }
            }
        }, 0, 10000);

    }

    void doWork() {
        checkWriteExternalPermission();
        System.out.println("Started the music Service  customMusic = " + customPath);
        if (customPath) {
            player = MediaPlayer.create(this, Uri.fromFile(new File(path)));
            player.setLooping(true);
        } else {
            player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
            player.setLooping(true);
        }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] pattern = new long[8];
            pattern[0] = 500;
            pattern[1] = 950;
            pattern[2] = 50;
            pattern[3] = 500;
            pattern[4] = 50;
            pattern[5] = 500;
            pattern[6] = 50;
            pattern[7] = 500;
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(10000);
        }
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        increaseVolumeWithTime(maxVol);
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

    void turnAudioON(int Volume) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //TODO change the index to change the volume level with passing time
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Volume, 0);
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
