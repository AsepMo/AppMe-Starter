package com.appme.story.application;

import android.Manifest;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Gravity;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.folders.FileMe;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.models.AppMe;
import com.appme.story.engine.app.models.VersionModel;
import com.appme.story.engine.app.tasks.FileScanningTask;
import com.appme.story.engine.app.tasks.CheckUpdateTask;
import com.appme.story.engine.app.permissions.PermissionsManager;
import com.appme.story.engine.app.permissions.PermissionsResultAction;
import com.appme.story.engine.app.commons.UpdateWrapper;
import com.appme.story.engine.app.utils.NetWorkUtils;
import com.appme.story.engine.widget.soundPool.SoundPoolManager;
import com.appme.story.receiver.SendBroadcast;

public class Application {
    private static final String TAG = Application.class.getSimpleName();
    private static volatile Application Instance = null;
    private Context context;
	private int SPLASH_TIME_OUT = 2000;
	
    public static Application getInstance() {
        Application localInstance = Instance;
        if (localInstance == null) {
            synchronized (Application.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new Application(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private Application(Context context) {
        this.context = context;
        FolderMe.with(context).initFolder();
    }

    public static Application with(Context context) {
        return new Application(context);
    }
    
    public void setInitialize(AppCompatActivity mActivity) {
		PackageManager packageManager = mActivity.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(mActivity.getPackageName(), PackageManager.GET_PERMISSIONS);
			File appFile = new File(packageInfo.applicationInfo.sourceDir);

			AppMe mAppMe = new AppMe();
			mAppMe.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
			mAppMe.setAppThumbnail(packageInfo.applicationInfo.loadIcon(packageManager));
			mAppMe.setAppSize(FileUtils.byteCountToDisplaySize(appFile.length()));
			mAppMe.setPackageName(packageInfo.packageName);
			mAppMe.setVersionName(packageInfo.versionName);
			mAppMe.setVersionCode(packageInfo.versionCode);
			mAppMe.setAppUpdate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(appFile.lastModified())).toString());
			mAppMe.setAppLocation(packageInfo.applicationInfo.sourceDir);
			mAppMe.setAppListNumber(1);
			mAppMe.initialise(mAppMe);
        	// Fill file stats (code, cache and data size)
			//getPackageStats(packageManager, packageInfo);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

    }

    public void setPermission(final Activity act, final String[] permissions, final OnActionPermissionListener mOnActionPermissionListener) {      
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(act, permissions, new PermissionsResultAction() {
                @Override
                public void onGranted() {
					new CountDownTimer(SPLASH_TIME_OUT, SPLASH_TIME_OUT){
						@Override
						public void onTick(long l) {

						}

						@Override
						public void onFinish() {  
							soundPlay(R.raw.sound_done);
							mOnActionPermissionListener.onGranted();
						}
					}.start();
                }


                @Override
                public void onDenied(final String permission) {
					new CountDownTimer(SPLASH_TIME_OUT, SPLASH_TIME_OUT){
						@Override
						public void onTick(long l) {

						}

						@Override
						public void onFinish() {  
							soundPlay(R.raw.sound_error);
							mOnActionPermissionListener.onDenied(permission);
						}
					}.start();
                }
            });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,  @NonNull int[] grantResults) {
        Log.i(TAG, "Activity-onRequestPermissionsResult() PermissionsManager.notifyPermissionsChange()");
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    public void setTransitionListener(final AppCompatActivity activity, final String message, final Class<?> mClass) {
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
	
    public void soundPlay(int sound) {
        SoundPoolManager.getInstance().playSound(sound);
    }


    public void soundRelease() {
        SoundPoolManager.getInstance().release();
    }

    public void check() {
        checkUpdate(0, null);
    }

    public void checkNow() {
        checkUpdate(5 * 60 * 1000, null);
    }

    public void checkCustoms() {
        checkUpdate(0, CustomsUpdateActivity.class);
    }

    public void checkUpdate(final long time, final Class<? extends AppCompatActivity> cls) {

        UpdateWrapper.Builder builder = new UpdateWrapper.Builder(AppController.getContext())
            .setTime(time)
            .setNotificationIcon(R.mipmap.ic_launcher_round)
            .setUrl("https://asepmo-story.000webhostapp.com/json/appme-update.json")
            .setIsShowToast(false)
            .setCallback(new CheckUpdateTask.Callback() {
                @Override
                public void callBack(VersionModel model, boolean hasNewVersion) {
                    Log.d(TAG, "has new version:" + hasNewVersion + ";version info :" + model.getVersionName());
                }
            });

        if (cls != null) {
            builder.setCustomsActivity(cls);
        }

        builder.build().start();
    }

    public static String[] requestPermissionConnection = new String[]
    {
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.CHANGE_WIFI_STATE
    };

    public static String[] requestPermissionStorage = new String[]
    {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String[] requestPermissionShortcut = new String[]
    {
        Manifest.permission.INSTALL_SHORTCUT,
        Manifest.permission.UNINSTALL_SHORTCUT
    };
    
    public void exitApplication(Context c) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }

    public interface OnActionPermissionListener {
        void onGranted();
        void onDenied(String permission);
    }

    public interface OnApplicationTaskListener {
        void onPreExecute();
        void onSuccess(ArrayList<AppMe> result);
        void onFailed();
        void isEmpty();
    }
}


