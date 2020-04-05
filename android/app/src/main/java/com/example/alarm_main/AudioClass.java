package com.example.alarm_main;

import android.content.Context;
import android.media.AudioManager;

import java.util.Timer;
import java.util.TimerTask;

class AudioClass {
    //To control Volume ...
     private AudioManager audioManager;
     private Timer t = new Timer();

    void initiateAudioManager(Context context) {
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
    }

    private void setVolume(int val) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, val, 0);
    }

    void setLowVolume() {
        t.cancel();
        setVolume(10);
    }

    void increaseVolumeWithTime() {
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        t.scheduleAtFixedRate(new TimerTask() {
            int Vol = 5;

            @Override
            public void run() {
                setVolume(Vol);
                if (!(Vol >= maxVol - 2)) {
                    Vol++;
                }
            }
        }, 0, 10000);

    }

}
