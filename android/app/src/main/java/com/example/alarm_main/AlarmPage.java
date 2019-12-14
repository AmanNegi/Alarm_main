package com.example.alarm_main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AlarmPage extends Activity {
    Button dismissButton;
    TextView wakeUpTextView, debugTextView, messageTextView;
    AudioManager audioManager;

    @Override
    protected void onStart() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "myApp:alarmApp");

        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock
                kl = km.newKeyguardLock("myApp:alarmApp");
        kl.disableKeyguard();
        wl.acquire(20000);


        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newactivity);

        System.out.println("In onCreate() [AlarmPage.java]");

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        dismissButton = findViewById(R.id.dismissButtonId);
        wakeUpTextView = findViewById(R.id.wakeUpTextView);
        debugTextView = findViewById(R.id.debugTextViewId);
        messageTextView = findViewById(R.id.messageTextView);

        int uniqueId = getIntent().getIntExtra("uniqueId", 0);
        String message = getIntent().getStringExtra("message");
        String timeString = getIntent().getStringExtra("timeString");
        Boolean repeating = getIntent().getBooleanExtra("repeating", false);

        debugTextView.setText(timeString);
        messageTextView.setText(message);

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioClass audioClass = new AudioClass();
                audioClass.initiateAudioManager(getApplicationContext());
                audioClass.setLowVolume();
                System.out.println("onClick in [AlarmPage.java]");

                System.out.println(" in AlarmPage.java behind the uniqueId " + (uniqueId));
                Intent intent
                        = new Intent(getApplicationContext(), MathsInterface.class);
                intent.putExtra("uniqueId", uniqueId);
                intent.putExtra("repeating",repeating);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                +WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "It ain't gonna work", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onUserLeaveHint() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //Do Nothing here
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //Do Nothing here
        }
        return true;

    }
}
