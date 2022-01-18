package com.appme.story;

import java.util.ArrayList;
import java.util.List;

import com.appme.story.application.Application;
import com.appme.story.application.ApplicationMain;
import com.appme.story.application.ApplicationSettings;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.analytics.Analytics;
import com.appme.story.engine.app.analytics.CrashHandler;
import com.appme.story.engine.app.commons.BaseActivity;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.utils.NetWorkUtils;
import com.appme.story.engine.widget.soundPool.SoundPoolManager;
import com.appme.story.engine.widget.soundPool.ISoundPoolLoaded;
import com.appme.story.settings.SharedPref;

public class AppController extends ApplicationMain {

    private static AppController sAppController;
    private Analytics mAnalytics = null;
	private Application mApplication = null;
	private FolderMe mFolderMe = null;
	private Engine mEngineMe = null;
	private ApplicationSettings applicationSettings;
    private static SharedPref appPreferences = null;
    @Override
    public void onCreate() {
        super.onCreate();
        
        sAppController = this;
        applicationSettings = new ApplicationSettings(this);
    }

    public static boolean isNightMode() {
        return getAppController().getResources().getBoolean(R.bool.night_mode);
    }

    public static AppController getAppController() {
        return sAppController;
    }

    @Override
    public void initAnalytics() {
        super.initAnalytics();
    }

    @Override
    public void initCrashHandler() {
        super.initCrashHandler();
		CrashHandler.init(this);
    }

    @Override
    public void initConfig() {
        super.initConfig();
        
    }

    @Override
    public void initFolder() {
        super.initFolder();
    }

    @Override
    public void initSoundManager() {
        super.initSoundManager();
        SoundPoolManager.CreateInstance();
        List<Integer> sounds = new ArrayList<Integer>();
        sounds.add(R.raw.sound_add);
        sounds.add(R.raw.sound_done);
        sounds.add(R.raw.sound_error);
        sounds.add(R.raw.sound_beep);
        sounds.add(R.raw.sound_click);
        sounds.add(R.raw.sound_jumping);
        sounds.add(R.raw.sound_jumping_failed);
        sounds.add(R.raw.sound_start_task);
        sounds.add(R.raw.sound_success_task);
        SoundPoolManager.getInstance().setSounds(sounds);
        try {
            SoundPoolManager.getInstance().InitializeSoundPool(getContext(), new ISoundPoolLoaded() {
                    @Override
                    public void onSuccess() {
                        SoundPoolManager.getInstance().playSound(R.raw.sound_success_task);
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }

        SoundPoolManager.getInstance().setPlaySound(true);
        
    }
   
	
	public Analytics getAnalytics() {
		if(mAnalytics == null){
			mAnalytics = Analytics.with(getContext());
		}
		return mAnalytics;
	}

	public Application getAppMe() {
		if(mApplication == null){
			mApplication = Application.with(getContext());
		}
		return mApplication;
	}

	public FolderMe getFolderMe(){
		if(mFolderMe == null){
			mFolderMe = FolderMe.with(getContext());
		}
		return mFolderMe;
	}
    
    public Engine getEngineMe(){
        if(mEngineMe == null){
            mEngineMe = Engine.with(getContext());
        }
        return mEngineMe;
	}
	
    public static String getServerIP() {
        return "http://" + NetWorkUtils.getLocalIpAddress() + ":" + sAppController.applicationSettings.getServerPort();
    }
    
    public static SharedPref getAppPreferences()
    {
        if (appPreferences == null)
            appPreferences = SharedPref.loadPreferences(getContext());

        return appPreferences;
    }
}
