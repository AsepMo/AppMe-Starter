package com.appme.story.application;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;

import com.appme.story.R;
import com.appme.story.engine.app.commons.BaseActivity;
import com.appme.story.engine.app.fragments.RootCheckerFragment;

public class RootCheckerActivity extends BaseActivity {

    public static final String TAG = "ApplicationActivity";
    public static void start(Context c){
        Intent mIntent = new Intent(c, RootCheckerActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(mIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_root_checker);
        switchFragment(RootCheckerFragment.newInstance());
    }


}
