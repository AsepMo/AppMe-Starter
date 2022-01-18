package com.appme.story.engine.app.commons;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public abstract class AbstractUpdateActivity extends AppCompatActivity {
    protected abstract Fragment getUpdateDialogFragment();

    protected abstract Fragment getDownLoadDialogFragment();
}
