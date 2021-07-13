package com.example.digitalspeedometer;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static PreferencesHelper sInstance;
    public SharedPreferences mPrefs;
    public SharedPreferences.Editor mEditor;
    private Context mContext;

    public PreferencesHelper(Context context) {
        mContext = context;
        mPrefs = this.mContext.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    public static synchronized PreferencesHelper getInstance(Context context){
        if(sInstance == null) {
            sInstance = new PreferencesHelper(context);
        }
        return  sInstance;
    }

    public int getSpeedUnit(){
       return mPrefs.getInt("speed_unit", 0);
    }

    public int getMaxSpeed(){
       return mPrefs.getInt("max_speed", 60);
    }

    public boolean getIsAlarmEnabled(){
        return  mPrefs.getBoolean("speed_alarm", false);
    }

    public boolean setSpeedUnit(int unit){
      try {
          mEditor.putInt("speed_unit", unit);
          mEditor.apply();
          return true;
      }catch (Throwable e){
          return false;
      }
    }

    public boolean setMaxSpeed(int speed){
        try {
            mEditor.putInt("max_speed", speed);
            mEditor.apply();
            return true;
        }catch (Throwable e){
            return false;
        }
    }

    public boolean setIsAlarmEnabled(boolean enabled){
        try {
            mEditor.putBoolean("speed_alarm", enabled);
            mEditor.apply();
            return true;
        }catch (Throwable e){
            return false;
        }
    }


}
