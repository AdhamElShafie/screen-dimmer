package com.example.screendimmerplz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.app.ActionBar;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import java.util.Random;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class MainActivity extends AppCompatActivity {

    private SharedMemory mSharedMemory;
    private ToggleButton mToggleButton;

    boolean black = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        Intent i = new Intent (MainActivity.this, ScreenDimmerService.class);
        startService(i);


        SeekBar brightness = findViewById(R.id.seek);

        mToggleButton = findViewById(R.id.start);
        mSharedMemory = new SharedMemory(this);

        mToggleButton.setChecked(ScreenDimmerService.STATE == ScreenDimmerService.STATE_ACTIVE);

        SeekBar.OnSeekBarChangeListener cl = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSharedMemory.setBrightness(brightness.getProgress());

                if (ScreenDimmerService.STATE == ScreenDimmerService.STATE_ACTIVE) {
                    ScreenDimmerService.updateScreenFilter(mSharedMemory);
                }

                mToggleButton.setChecked(ScreenDimmerService.STATE == ScreenDimmerService.STATE_ACTIVE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        brightness.setOnSeekBarChangeListener(cl);

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScreenDimmerService.STATE == ScreenDimmerService.STATE_ACTIVE) {
                    ScreenDimmerService.removeScreenFilter();
                    ScreenDimmerService.STATE = ScreenDimmerService.STATE_INACTIVE;
                }
                else {
                    ScreenDimmerService.activateScreenFilter(mSharedMemory);
                    ScreenDimmerService.STATE = ScreenDimmerService.STATE_ACTIVE;
                }

            }
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }
    }

    public void changeColor(){
        RelativeLayout fl = findViewById(R.id.rlVar);
        int col = 0;
        if (black) {
            col = ContextCompat.getColor(this.getApplicationContext(), R.color.purple_700);
        }else{
            col = ContextCompat.getColor(this.getApplicationContext(), R.color.bigpp);
        }
        black = !black;
        fl.setBackgroundColor(col);
    }
}