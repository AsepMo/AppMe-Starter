package com.appme.story.application;

import android.support.v4.app.Fragment;

import com.appme.story.engine.app.fragments.CustomsUpdateFragment;

public class CustomsUpdateActivity extends UpdateActivity {
    @Override
    protected Fragment getUpdateDialogFragment() {
        return CustomsUpdateFragment.newInstance(mModel,"当前已经是最新版本");
    }
}
