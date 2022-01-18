package com.appme.story.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Comparator;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.engine.app.folders.root.RootChecker;

public class Settings {
    
    public static String TAG = Settings.class.getSimpleName();
    public static final String NAME = "SharedPref";
    private static SharedPreferences mPrefs;

    public static int mTheme;

    public static void updatePreferences(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        mTheme = Integer.parseInt(mPrefs.getString("preference_theme", Integer.toString(R.style.AppTheme_NoActionBar)));

        rootAccess();
    }
    
    public static String getWebFileMd5()
    {
        SharedPreferences sp = AppController.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);       
        return sp.getString("web_file_md5", "");
    }
    
    public static void setWebFileMd5(String md5)
    {
        SharedPreferences sp = AppController.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("web_file_md5", md5).commit();
        editor.commit();      
    }
    
    public static int getPort()
    {
        return mPrefs.getInt("server_port", 8080);
    }
    
    public static void setPort(int port){
        mPrefs.edit().putInt("server_port", port).commit();
	}
    
    public static boolean rootAccess() {
        return mPrefs.getBoolean("enablerootaccess", false) && RootChecker.isDeviceRooted();
    }
    
    public static String getDefaultDir() {
        return mPrefs.getString("defaultdir", Environment.getExternalStorageDirectory().getPath());
    }
    
    public static int getDefaultTheme() {
        return mTheme;
    }

    public static void setDefaultTheme(int theme) {
        mTheme = theme;
    }
}
