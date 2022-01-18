package com.appme.story.application;

import android.os.Bundle;
import android.os.Handler;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.appme.story.R;
import com.appme.story.application.Application;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.commons.BaseActivity;
import com.appme.story.engine.app.folders.AssetManager;
import com.appme.story.engine.app.listeners.OnRequestHandlerListener;
import com.appme.story.engine.widget.SplashScreen;
import com.appme.story.settings.Settings;

public class ApplicationStarter extends BaseActivity {

	private static final String TAG = ApplicationStarter.class.getSimpleName();
    private SplashScreen mSplashScreen;
    private int SPLASH_TIME_OUT = 5000;
	private Handler mHandler = new Handler();
	private Runnable mRunner = new Runnable() {

		/*
		 * Showing splash screen with a timer. This will be useful when you
		 * want to show case your app logo / company
		 */

		@Override
		public void run() {
			// This method will be executed once the timer is over
			// Start SplashScreen
		    mSplashScreen.start();
			mSplashScreen.setOnSplashScreenListener(new SplashScreen.OnSplashScreenListener(){
					@Override
					public void OnStartActivity(final SplashScreen mSplash) {
						/*boolean isRemove = getFolderMe().remove(getFolderMe().getExternalFileDir("all"));
                         if(!isRemove){
                         Toast.makeText(ApplicationStarter.this, "Clean Directory", Toast.LENGTH_SHORT).show();                          				
                         }*/
                        getAppMe().setPermission(ApplicationStarter.this, Application.requestPermissionShortcut, new Application.OnActionPermissionListener(){
                                @Override
                                public void onGranted() {   
                                    getEngineMe().createShortCut();                                                              
                                }

                                @Override
                                public void onDenied(String permission) {

                                }
                            });	
                        mSplash.createShortCut();        
						switchActivity(ApplicationStarter.this, "Install Binary Index File", ApplicationInstaller.class);
					}
				});
		}  
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        //Theme.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_starter);

        initToolbar();
        // Toolbar is Gone
        getToolbar().setVisibility(View.GONE);

        mSplashScreen = (SplashScreen)findViewById(R.id.splash_screen);
        AssetManager.with(ApplicationStarter.this).extractAssets().setOnAssetsManagerListener(new AssetManager.OnAssetsManagerListener(){
                @Override
                public void onSuccess(String path) {
                    setRequestHandler(new OnRequestHandlerListener(){
                            @Override
                            public void onHandler() {

                                mHandler.postDelayed(mRunner, SPLASH_TIME_OUT);
                            }
                        });              
                }

                @Override
                public void onFail(String path) {
                    //setSplashScreen();
                }
            });

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume:");  
		mHandler.postDelayed(mRunner, SPLASH_TIME_OUT);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause:"); 
		mHandler.removeCallbacks(mRunner);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy:"); 
		mHandler.removeCallbacks(mRunner);
    }

}
