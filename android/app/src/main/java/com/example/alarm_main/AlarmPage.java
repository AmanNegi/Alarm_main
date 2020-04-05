package com.example.alarm_main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmPage extends Activity {
    Button dismissButton;
    TextView wakeUpTextView, timeTextView, messageTextView;
    ImageView imageView;
    static boolean isOpened = false;
    private PowerManager powerManager;
    private KeyguardManager.KeyguardLock keyguardLock;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onStart() {
        super.onStart();
        FlutterEngineClass.createFlutterEngine(getApplicationContext());
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        keyguardLock = ((KeyguardManager)
                getSystemService(KEYGUARD_SERVICE))
                .newKeyguardLock(KEYGUARD_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.FULL_WAKE_LOCK,
                "myApp:wakeLockOne");

        if (!powerManager.isScreenOn()) {
            keyguardLock.disableKeyguard();
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
            isOpened = true;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newactivity);

        dismissButton = findViewById(R.id.dismissButtonId);
        wakeUpTextView = findViewById(R.id.wakeUpTextView);
        timeTextView = findViewById(R.id.debugTextViewId);
        messageTextView = findViewById(R.id.messageTextView);
        imageView = findViewById(R.id.imageView);


        int uniqueId = getIntent().getIntExtra("uniqueId", 0);
        String message = getIntent().getStringExtra("message");
        String timeString = getIntent().getStringExtra("timeString");
        boolean repeating = getIntent().getBooleanExtra("repeating", false);
        boolean defaultMethod = getIntent().getBooleanExtra("defaultMethod", true);
        if (timeString != null && timeString.length() != 0) {
            timeTextView.setText(timeString);
            messageTextView.setText(message);
        } else {
            timeTextView.setText("");
            messageTextView.setText("");
        }

        System.out.println(" The value of defaultMethod in AlarmPage " + defaultMethod);
        dismissButton.setOnClickListener(view -> {
            if (defaultMethod) {
                //Cancel alarm right here
                AlarmHelper.deleteAlarm(uniqueId);

                Intent musicService = new Intent(getApplicationContext(), MusicService.class);
                stopService(musicService);

                Intent myService = new Intent(getApplicationContext(), NotificationService.class);
                stopService(myService);

                finish();
            } else {
                //go to the maths page
                Intent intent
                        = new Intent(AlarmPage.this, MathsInterface.class);
                intent.putExtra("uniqueId", uniqueId);
                intent.putExtra("repeating", repeating);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onAttachedToWindow() {
        //   super.onAttachedToWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
        super.onPause();
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

    @Override
    protected void onDestroy() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        isOpened = false;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
