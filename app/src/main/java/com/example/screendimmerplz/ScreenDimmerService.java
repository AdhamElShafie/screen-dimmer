package com.example.screendimmerplz;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class ScreenDimmerService extends Service {

    public static int STATE_ACTIVE = 1;
    public static int STATE_INACTIVE = 0;

    public static int STATE;

    private SharedMemory mSharedMemory;

    static RelativeLayout relativeLayout;
    static WindowManager windowManager;
    static WindowManager.LayoutParams layoutParams;


    public ScreenDimmerService() {
        STATE = STATE_INACTIVE;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createScreenFilter();

        STATE = STATE_INACTIVE;
    }

    @SuppressWarnings(value = "all")
    private void createScreenFilter() {
        WindowManager windowManager_tmp = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        int temp = getNavigationBarHeight(getApplicationContext());
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager_tmp.getDefaultDisplay().getRealMetrics(metrics);
        layoutParams.width = metrics.heightPixels + temp;
        layoutParams.height = metrics.heightPixels + temp;

        layoutParams.format = PixelFormat.TRANSLUCENT;

        LayoutInflater layoutInflater = LayoutInflater.from(getApplication());
        relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.screen_dimmer, null);
    }

    public static int getNavigationBarHeight(Context context) {
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }

    public static void updateScreenFilter(SharedMemory mSharedMemory) {
        updateThisSettings(mSharedMemory);
        windowManager.updateViewLayout(relativeLayout, layoutParams);
    }

    public static void activateScreenFilter(SharedMemory mSharedMemory) {
        updateThisSettings(mSharedMemory);
        windowManager.addView(relativeLayout, layoutParams);
    }


    private static void updateThisSettings(SharedMemory mSharedMemory) {
        layoutParams.alpha = mSharedMemory.getBrightness();
        relativeLayout.setBackgroundColor(mSharedMemory.getAlpha());

        layoutParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

    }

    public static void removeScreenFilter() {
        windowManager.removeViewImmediate(relativeLayout);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            windowManager.removeViewImmediate(relativeLayout);
        } catch (Exception ignored) {
        }
        STATE=STATE_INACTIVE;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}