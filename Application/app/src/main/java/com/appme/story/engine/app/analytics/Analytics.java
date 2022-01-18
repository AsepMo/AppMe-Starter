package com.appme.story.engine.app.analytics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.folders.FileMe;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.models.AppMe;
import com.appme.story.engine.app.models.VideoData;
import com.appme.story.engine.app.models.VersionModel;
import com.appme.story.engine.app.tasks.VideoTask;
import com.appme.story.engine.app.tasks.CheckUpdateTask;

public class Analytics {

    private static final String TAG = Analytics.class.getSimpleName();
    private static volatile Analytics Instance = null;
    private Context context;
	private SharedPreferences mSharedPreference;
	/** An intent for launching the system settings. */
    private static final Intent sSettingsIntent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);

    public static Analytics getInstance() {
        Analytics localInstance = Instance;
        if (localInstance == null) {
            synchronized (Analytics.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new Analytics(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private Analytics(Context context) {
        this.context = context;
		mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);

        FolderMe.with(context).initFolder();
    }

    public static Analytics with(Context context) {
        return new Analytics(context);
    }

	public Analytics setAnalytisActivity(OnFirstTimeListener mOnFirstTimeListener) {
        /**** START APP ****/
		boolean isFirstStart = mSharedPreference.getBoolean("firstStart", true);
        if (isFirstStart) {
            SharedPreferences.Editor e = mSharedPreference.edit();
            e.putBoolean("firstStart", false);
            e.apply();
            if (mOnFirstTimeListener != null) {
				mOnFirstTimeListener.onFirsTime();
			}
		} else {
			if (mOnFirstTimeListener != null) {
				mOnFirstTimeListener.onSecondTime();
			}
		}
		return this;
	}

	public interface OnFirstTimeListener {
		void onFirsTime();
		void onSecondTime();
	}
}
