package com.appme.story;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import com.appme.story.R;
import com.appme.story.application.Application;
import com.appme.story.application.ApplicationStarter;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.commons.BaseActivity;
import com.appme.story.engine.app.analytics.Analytics;
import com.appme.story.engine.app.models.AppMe;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.listeners.OnRequestHandlerListener;

public class SplashActivity extends BaseActivity {

    public static final String TAG = "ApplicationActivity";
    public static void start(Context c) {
        Intent mIntent = new Intent(c, SplashActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(mIntent);
    }
	
    private ImageView mAppIcon;
    private TextView mAppMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initToolbar();

	    mAppIcon = (ImageView) findViewById(R.id.splash_app_icon);
		mAppIcon.setImageResource(R.drawable.apk_v2);

        mAppMessage = (TextView) findViewById(R.id.app_message);
		mAppMessage.setText("Welcome To AppMe");

		getAppMe().setPermission(SplashActivity.this, Application.requestPermissionStorage, new Application.OnActionPermissionListener(){
				@Override
				public void onGranted() {	
                    getAnalytics().setAnalytisActivity(new Analytics.OnFirstTimeListener(){
							@Override
							public void onFirsTime() {
                                setLogger("This First Time Launching");
								getFolderMe().initFolder();
								getAppMe().setInitialize(SplashActivity.this);
								switchActivity(SplashActivity.this, mAppMessage, "This First Time Launching ", ApplicationStarter.class);	

							}
							@Override
							public void onSecondTime() {
                                setLogger("Open AppMe");
								getFolderMe().initFolder();
								getAppMe().setInitialize(SplashActivity.this);						
								setFileScanning();
							}
						});			
				}

				@Override
				public void onDenied(String permission) {
                    setLogger(permission);
				}
			});	
    }

	public void setFileScanning() {
		boolean isExist = AppMe.isExist(AppMe.KEY_APPLIST_JSON);
        if (isExist) {		
			switchActivity(SplashActivity.this, mAppMessage, "Start Activity", ApplicationActivity.class);			           
		} else {
            setRequestHandler(new OnRequestHandlerListener(){
                    @Override
                    public void onHandler() {
                        mAppMessage.setText("Apk File Scanning In Memory"); 
                        Engine engineMe = Engine.with(SplashActivity.this);
                        engineMe.startScanning();
                        engineMe.setOnScanningTaskListener(new Engine.OnScanningTaskListener(){
                                @Override
                                public void onMemoryScanning(String message) {

                                }

                                @Override
                                public void onMemoryScanningSuccess(ArrayList<AppMe> result) {
                                    mAppMessage.setText("Apk File Scanning In Memory Success");                     
                                }

                                @Override
                                public void onMemoryScanningFailed(String message) {
                                    mAppMessage.setText("Apk File Scanning In Memory Failed");  
                                    //Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();       
                                } 

                                @Override
                                public void onMemoryIsEmpty(String message) {
                                    mAppMessage.setText("Apk File In Memory is Empty"); 
                                    Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();       
                                } 

                                @Override
                                public void onSdCardScanning(String message) {
                                    mAppMessage.setText("Apk File Scanning In Sdcard"); 
                                    //Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();                              
                                }

                                @Override
                                public void onSdCardScanningSuccess(ArrayList<AppMe> result) {
                                    mAppMessage.setText("Apk File Scanning In Sdcard Success"); 
                                    setRequestHandler(new OnRequestHandlerListener(){
                                            @Override
                                            public void onHandler() {
                                                switchActivity(SplashActivity.this, mAppMessage, "Start Activity", ApplicationActivity.class);                                                
                                            }
                                        });              

                                }

                                @Override
                                public void onSdCardScanningFailed(String message) {
                                    mAppMessage.setText("Apk File Scanning In Sdcard Failed");  
                                    //Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();       
                                } 

                                @Override
                                public void onSdCardIsEmpty(String message) {
                                    mAppMessage.setText("Apk File In SdCard Is Empty"); 
                                    //Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();       
                                } 
                            });
                    }
                });              
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}
