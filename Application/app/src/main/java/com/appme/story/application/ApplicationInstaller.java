package com.appme.story.application;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.appme.story.R;
import com.appme.story.engine.app.commons.BaseActivity;
import com.appme.story.engine.app.fragments.BinaryInstallerFragment;
import com.appme.story.settings.Settings;

public class ApplicationInstaller extends BaseActivity {

	public static String TAG = ApplicationInstaller.class.getSimpleName();

    public static void start(Context c) {
        Intent mIntent = new Intent(c, ApplicationInstaller.class);
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(mIntent);
    }

    private ImageView appIcon;
	private TextView appName;
	private TextView packageName;
	private TextView versionName;
	private TextView versionCode;
	private TextView author;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_installation);

		 initToolbar();
		
		 appIcon = (ImageView)findViewById(R.id.app_icon);
		 appIcon.setImageResource(R.mipmap.ic_launcher);

		// output
		StringBuilder sb1 = (new StringBuilder()).append("Name   :").append("   ").append("AppMe");
		appName = (TextView)findViewById(R.id.app_name);
		appName.setText(sb1.toString());
		StringBuilder sb2 = (new StringBuilder()).append("PackageName   :").append("   ").append("com.appme.story");
		packageName = (TextView)findViewById(R.id.package_name);
		packageName.setText(sb2.toString());
		
		StringBuilder sb3 = (new StringBuilder()).append("VersionName :").append("   ").append("1.0");
		versionName = (TextView)findViewById(R.id.version_name);
		versionName.setText(sb3.toString());
		StringBuilder sb4 = (new StringBuilder()).append("VersionCode :").append("   ").append("1");
		versionCode = (TextView)findViewById(R.id.version_code);
		versionCode.setText(sb4.toString());
		StringBuilder sb5 = (new StringBuilder()).append("Author   :").append("   ").append("AsepMo");
		author = (TextView)findViewById(R.id.author);
		author.setText(sb5.toString());
		switchFragment(new BinaryInstallerFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume:");  
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause:");
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy:");
        
    }
}
