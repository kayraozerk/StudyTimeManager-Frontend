package com.example.cs310_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StopwatchActivity extends AppCompatActivity {

    private TextView stopwatchTimer;
    private Button startButton, stopButton, backButton;
    private long startTime = 0L, elapsedTime = 0L;
    private boolean isRunning;
    private Handler customHandler = new Handler();
    private SharedPreferences stopwatchPreferences;
    private String courseName;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            updateStopwatchDisplay(elapsedTime);
            if (isRunning) {
                customHandler.postDelayed(this, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        stopwatchTimer = findViewById(R.id.stopwatchTimer);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        backButton = findViewById(R.id.backButton);

        // Retrieve the course name passed from MainActivity
        courseName = getIntent().getStringExtra("COURSE_NAME");
        stopwatchPreferences = getSharedPreferences("StopwatchPrefs_" + courseName, MODE_PRIVATE);

        restoreStopwatchState();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                startTime = SystemClock.uptimeMillis() - elapsedTime;
                customHandler.postDelayed(updateTimerThread, 0);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;
                customHandler.removeCallbacks(updateTimerThread);
                saveStopwatchState();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveStopwatchState();
    }

    private void restoreStopwatchState() {
        isRunning = stopwatchPreferences.getBoolean("isRunning", false);
        elapsedTime = stopwatchPreferences.getLong("elapsedTime", 0L);

        if (isRunning) {
            startTime = SystemClock.uptimeMillis() - elapsedTime;
            customHandler.postDelayed(updateTimerThread, 0);
        } else {
            updateStopwatchDisplay(elapsedTime);
        }
    }

    private void saveStopwatchState() {
        SharedPreferences.Editor editor = stopwatchPreferences.edit();
        editor.putBoolean("isRunning", isRunning);
        editor.putLong("elapsedTime", elapsedTime);
        editor.apply();
    }

    private void updateStopwatchDisplay(long elapsedTime) {
        int secs = (int) (elapsedTime / 1000);
        int mins = secs / 60;
        secs %= 60;
        int hours = mins / 60;
        mins %= 60;
        stopwatchTimer.setText(String.format("%02d:%02d:%02d", hours, mins, secs));
    }
}
