package com.example.alarm_main;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    MediaPlayer player;
    Vibrator vibrator;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("Started the music Service");
        player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        player.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000010000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(10000);
        }
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        increaseVolumeWithTime(maxVol);
    }


    public void increaseVolumeWithTime(final int maxVol) {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            int Vol = 5;

            @Override
            public void run() {
                turnAudioON(Vol);
                if (!(Vol >= maxVol-2)) {
                    Vol++;
                }
            }
        }, 0, 5000);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
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
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(this, wakeFulReceiver.class);
        sendBroadcast(intent);
    }
}
