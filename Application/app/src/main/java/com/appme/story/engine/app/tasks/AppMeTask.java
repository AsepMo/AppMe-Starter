package com.appme.story.engine.app.tasks;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import com.appme.story.engine.app.models.AppMe;
import org.json.JSONException;

public class AppMeTask extends AsyncTask<File, AppMe, ArrayList<AppMe>> {

    public static final String TAG = AppMeTask.class.getSimpleName();


    private Context mContext;
    private ArrayList<AppMe> appMe;
    private static final int SLEEP_TIME = 70;
    private OnAppMeTaskListener mOnAppMeTaskListener;

    public AppMeTask(Context context) {
        this.mContext = context; 
        
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute(); 
        if (mOnAppMeTaskListener != null) {
            mOnAppMeTaskListener.onPreExecute();
        }
    }

    @Override
    protected void onProgressUpdate(AppMe...values) {
        super.onProgressUpdate(values);
        if (mOnAppMeTaskListener != null) {
            mOnAppMeTaskListener.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected ArrayList<AppMe> doInBackground(File...file) {
        try {
            ArrayList<AppMe> appList = AppMe.loadFromFile(file[0].getAbsolutePath());
            appMe = new ArrayList<AppMe>();
            for (int i = 0; i < appList.size(); i++) {
                try {                  
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {

                }
                AppMe mAppMe  = new AppMe();
                mAppMe.setAppListNumber(appList.get(i).getAppListNumber());
                mAppMe.setAppName(appList.get(i).getAppName());
                //mAppMe.setAppUri(Uri.parse(file.getAbsolutePath()));
                mAppMe.setAppLocation(appList.get(i).getAppLocation());                  
                mAppMe.setAppIcon(appList.get(i).getAppIcon());   
                mAppMe.setAppSize(appList.get(i).getAppSize());  
                mAppMe.setAppUpdate(appList.get(i).getAppUpdate());
                appMe.add(mAppMe);
                
                publishProgress(mAppMe); 
           }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            if (mOnAppMeTaskListener != null) {
                mOnAppMeTaskListener.onFailed();
            }
        }
        return appMe;
    }

    @Override
    protected void onPostExecute(ArrayList<AppMe> isExist) {
        super.onPostExecute(isExist);
        if (mOnAppMeTaskListener != null) {
            mOnAppMeTaskListener.onSuccess(isExist);
        }
    }

    public void setOnAppMeTaskListener(OnAppMeTaskListener mOnAppMeTaskListener) {
        this.mOnAppMeTaskListener = mOnAppMeTaskListener;
    }

    public interface OnAppMeTaskListener {
        void onPreExecute();
        void onProgressUpdate(AppMe items);
        void onSuccess(ArrayList<AppMe> success);
        void onFailed();
        void isEmpty();
    }
}
