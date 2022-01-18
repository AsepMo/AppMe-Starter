package com.appme.story.application;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.appme.story.R;
import com.appme.story.engine.app.commons.BaseActivity;
import com.appme.story.engine.app.fragments.AppMeFragment;
import com.appme.story.engine.app.fragments.RootCheckerFragment;
import com.appme.story.engine.app.fragments.CustomTabsHelperFragment;
import com.appme.story.engine.app.folders.preview.IconPreview;
import com.appme.story.receiver.AppMeReceiver;
import com.appme.story.receiver.SendBroadcast;
import com.appme.story.service.AppMeService;
import com.appme.story.service.ServiceUtils;
import com.appme.story.service.CustomTabsActivityHelper;
import com.appme.story.settings.Settings;

public class ApplicationActivity extends BaseActivity implements AppMeReceiver.OnSendBroadcastListener {

    public static String TAG = ApplicationActivity.class.getSimpleName();
    public final static String ACTION_CHROME_TABS = "com.appme.story.application.ACTION_CHROME_TABS";
    public final static String EXTRA_URL = "EXTRA_URL";
    
    public static void start(Context c) {
        Intent mIntent = new Intent(c, ApplicationActivity.class);
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        c.startActivity(mIntent);
    }

    public static void startChrome(Context c, String url) {
        Intent mIntent = new Intent(c, ApplicationActivity.class);
        mIntent.setAction(ACTION_CHROME_TABS);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.putExtra(EXTRA_URL, url);
        c.startActivity(mIntent);
    }

    private AppMeReceiver mAppMeReceiver;
    //private static final Uri PROJECT_URI = Uri.parse("https://github.com/DreaminginCodeZH/CustomTabsHelper");

    private final CustomTabsActivityHelper.CustomTabsFallback mCustomTabsFallback = new CustomTabsActivityHelper.CustomTabsFallback() {
        @Override
        public void openUri(Activity activity, Uri uri) {
            //showToast(R.string.custom_tabs_failed);
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                //showToast(R.string.activity_not_found);               
            }
        }
    };
    private CustomTabsHelperFragment mCustomTabsHelperFragment;
    private CustomTabsIntent mCustomTabsIntent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppController().getAppPreferences();
        setContentView(R.layout.activity_application);

        initToolbar();

        String action = getIntent().getAction();
        String title = "";
        String subTitle = "";
        int mColorPrimary;
        if (action != null && action.equals(ACTION_CHROME_TABS)) {
            title = "Monitor";
            subTitle = "Activity";
            mColorPrimary = R.color.colorPrimary;
            String url = getIntent().getStringExtra(EXTRA_URL);
            final Uri PROJECT_URI = Uri.parse(url);
            
            mCustomTabsHelperFragment = CustomTabsHelperFragment.attachTo(this);
            mCustomTabsIntent = new CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setToolbarColor(mColorPrimary)
                .setShowTitle(true)
                .build();

            mCustomTabsHelperFragment.setConnectionCallback(new CustomTabsActivityHelper.ConnectionCallback() {
                    @Override
                    public void onCustomTabsConnected() {
                        mCustomTabsHelperFragment.mayLaunchUrl(PROJECT_URI, null, null);
                    }
                    @Override
                    public void onCustomTabsDisconnected() {}
                });

            CustomTabsHelperFragment.open(ApplicationActivity.this, mCustomTabsIntent, PROJECT_URI, mCustomTabsFallback);

        } 
        Application app = Application.with(this);
        app.checkCustoms();    

        if (ServiceUtils.isServiceRunning(this)) {          
            ServiceUtils.getInstance().onStartAppMeService(); 
        } else {
            ServiceUtils.getInstance().launchAppMeService();
        }

		mAppMeReceiver = new AppMeReceiver();
        mAppMeReceiver.setOnSendBroadcastListener(this);
        registerBroadcastReceiver();



        switchFragment(new AppMeFragment());      
        new IconPreview(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        new IconPreview(this).clearCache();
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Chrome")
            .setIcon(R.drawable.ic_google_chrome)
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return true;
                }
            }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
    }


    public void registerBroadcastReceiver() {
        IntentFilter statusIntentFilter = new IntentFilter(SendBroadcast.PROCESS_BROADCAST_ACTION);
        registerReceiver(mAppMeReceiver, statusIntentFilter);
    }

    @Override
    public void onServiceReady(String message) {
        showToast(message);     
    }

    @Override
    public void onStartService(String message) {
        showToast(message);
    }

    @Override
    public void onStartActivity(String message) {
        showToast(message);
    }

    @Override
    public void onOpenBrowser(String message) {
        showToast(message);
    }

    @Override
    public void onPauseService(String message) {
        showToast(message);
    }

    @Override
    public void onResumeService(String message) {
        showToast(message);
    }

    @Override
    public void onStopService(String message) {
        showToast(message);
    }

    @Override
    public void onServiceShutDown(String message) {
        showToast(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume:");  
        registerBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause:");
        //unregisterReceiver(mAppMeReceiver); 
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy:");
        unregisterReceiver(mAppMeReceiver); 
    }

}
