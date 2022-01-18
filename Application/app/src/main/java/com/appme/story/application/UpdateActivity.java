package com.appme.story.application;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.appme.story.R;
import com.appme.story.engine.app.commons.AbstractUpdateActivity;
import com.appme.story.engine.app.commons.Constant;
import com.appme.story.engine.app.models.VersionModel;
import com.appme.story.engine.app.utils.PackageUtils;
import com.appme.story.engine.app.dialog.UpdateDialog;
import com.appme.story.engine.app.dialog.DownLoadDialog;

public class UpdateActivity extends AbstractUpdateActivity implements DownLoadDialog.OnFragmentOperation{
    
    public static final String TAG = "UpdateActivity";
    private int notificationIcon;
    protected VersionModel mModel;
    protected String mToastMsg;
    protected boolean mIsShowToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_update);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(false);
        notificationIcon = getIntent().getIntExtra(Constant.NOTIFICATION_ICON, 0);
        mModel = (VersionModel) getIntent().getSerializableExtra(Constant.MODEL);
        mToastMsg = getIntent().getStringExtra(Constant.TOAST_MSG);
        mIsShowToast = getIntent().getBooleanExtra(Constant.IS_SHOW_TOAST_MSG, true);
        if (mModel == null) {
            finish();
            return;
        }

        showUpdateDialog();

    }

    private void showUpdateDialog() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, getUpdateDialogFragment())
                .commit();
    }

    public void showDownLoadProgress() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, getDownLoadDialogFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (PackageUtils.getVersionCode(getApplicationContext()) < mModel.getMinSupport()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected Fragment getUpdateDialogFragment() {
        return UpdateDialog.newInstance(mModel, mToastMsg, mIsShowToast);
    }

    @Override
    protected Fragment getDownLoadDialogFragment() {
        return DownLoadDialog.newInstance(mModel.getUrl(), notificationIcon, PackageUtils.getVersionCode(getApplicationContext()) < mModel.getMinSupport());
    }

    @Override
    public void onFailed() {
        showUpdateDialog();
    }
    
    
}
