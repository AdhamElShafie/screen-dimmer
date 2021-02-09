package com.example.screendimmerplz;

import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Color;

public class SharedMemory {
    private SharedPreferences mSharedPreferences;

    public SharedMemory(Context context){
        mSharedPreferences = context.getSharedPreferences("SCREEN_FILTER_PREF", Context.MODE_PRIVATE);
    }

    public int getBrightness(){
        return mSharedPreferences.getInt("brightness", 0x33);
    }

    public void setBrightness(int v){
        mSharedPreferences.edit().putInt("brightness", v).apply();
    }

    public int getAlpha(){
        return Color.argb(getBrightness(), 0, 0, 0);
    }


}
