package com.example.alarm_main;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AlarmPage extends Activity {

    static boolean isRunning = false;
    Button dismissButton;
    TextView wakeUpTextView, debugTextView,messageTextView;
    AudioManager audioManager;

    @Override
    protected void onStart() {
        isRunning = true;
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
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

        int hour = getIntent().getIntExtra("hour", 0);
        int minute = getIntent().getIntExtra("minute", 0);
        String message = getIntent().getStringExtra("message");

        debugTextView.setText(hour + " : " + minute);
        messageTextView.setText(message);

        //setting fonts
        // Typeface lightFont = Typeface.createFromAsset(this.getAssets(), "fonts/Raleway-Light.ttf");
        // Typeface boldFont = Typeface.createFromAsset(this.getAssets(), "fonts/Raleway-Bold.ttf");
        //  wakeUpTextView.setTypeface(lightFont);
        //dismissButton.setTypeface(boldFont);

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("onClick in [AlarmPage.java]");

                Intent musicService = new Intent(getApplicationContext(), MusicService.class);
                stopService(musicService);

                Intent myService = new Intent(getApplicationContext(), NotificationService.class);
                stopService(myService);
                finish();

            }
        });

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
