package com.appme.story.engine.app.commons;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.application.Application;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.analytics.Analytics;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.models.AppMe;
import com.appme.story.engine.app.listeners.OnRequestHandlerListener;
import com.appme.story.engine.app.utils.Utils;
import com.appme.story.engine.app.utils.ToastUtils;
import com.appme.story.receiver.RemoteLogger;

public abstract class BaseActivity extends AppCompatActivity {

	private ActionBar mActionBar;
	private Toolbar mToolbar;
	public int SPLASH_TIME_OUT = 5000;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
    }

    public void initStatusBar() {
        Utils.setStatusBarLightMode(this, getResources().getBoolean(R.bool.light_status_bar));
    }

    public void initActionBar(String title) {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
			mActionBar.setTitle(title);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

	public void initToolbar() {
		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		if (mToolbar == null) {
			ActionBar mActionBar = getSupportActionBar();
			mActionBar.setTitle(null);
			setSupportActionBar(mToolbar);
		}
		final TextView mAppName = (TextView) findViewById(R.id.app_title);
		mAppName.setText(getString(R.string.app_name));
	}
	
	public void initToolbar(String title) {
		Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
		if (mToolbar == null) {
		    mActionBar = getSupportActionBar();
			mActionBar.setTitle(null);
			setSupportActionBar(mToolbar);
		}
		final TextView mAppName = (TextView) findViewById(R.id.app_title);
		mAppName.setText(title);
	}

	public ActionBar getActionBarMe() {
		return mActionBar;
	}

	public Toolbar getToolbar() {
		return mToolbar;
	}
	
    public AppController getAppController() {
        return AppController.getAppController();
    }

	public Application getAppMe()
	{
		if (getAppController() == null) return null;
		return getAppController().getAppMe();
	}
	
	public Analytics getAnalytics()
	{
		if (getAppController() == null) return null;
		return getAppController().getAnalytics();
	}
	
	public FolderMe getFolderMe()
	{
		if (getAppController() == null) return null;
		return getAppController().getFolderMe();
	}
	
    public Engine getEngineMe()
    {
        if (getAppController() == null) return null;
        return getAppController().getEngineMe();
	}
    
	public void switchActivity(final AppCompatActivity activity, final String message, final Class<?> mClass) {
		new CountDownTimer(2000, 2000){
			@Override
			public void onTick(long l) {

			}

			@Override
			public void onFinish() {
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();       

				Intent mIntent = new Intent(activity, mClass);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(mIntent);
				activity.finish();
			}  
		}.start();
    }
    
    public void switchActivity(final AppCompatActivity activity, final TextView mMessage, final String message, final Class<?> mClass) {
        mMessage.setText(message);
        new CountDownTimer(2000, 2000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();       

                Intent mIntent = new Intent(activity, mClass);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(mIntent);
                activity.finish();
            }  
        }.start();
    }

	public void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    }
    
    public void showToast(String message){
        ToastUtils.show(AppController.getContext(), message);       
    }
    
    public void setLogger(String log){
        RemoteLogger.sendBroadcast(AppController.getContext(), log);
    }
    
    public void setRequestHandler(final OnRequestHandlerListener mOnRequestHandlerListener){
        new CountDownTimer(2000, 2000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
               if(mOnRequestHandlerListener != null){
                   mOnRequestHandlerListener.onHandler();
               }
            }  
        }.start();
    }

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		getAppMe().onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

    public void onHomeItemClick(MenuItem item) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onHomeItemClick(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
