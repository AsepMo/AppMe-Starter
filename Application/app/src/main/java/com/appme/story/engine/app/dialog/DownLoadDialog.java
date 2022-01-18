package com.appme.story.engine.app.dialog;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appme.story.R;
import com.appme.story.engine.app.commons.Constant;
import com.appme.story.engine.app.tasks.DownLoadTask;
import com.appme.story.engine.app.folders.FileMe;
import com.appme.story.engine.app.utils.ToastUtils;
import com.appme.story.service.DownLoadService;

import java.io.File;

public class DownLoadDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "DownLoadDialog";
    private Activity mActivity;
    private Context mContext;
    private String mDownloadUrl;
    private int notificationIcon;
    private int currentProgress;
    private Button mBtnCancel;
    private Button mBtnBackground;
    private TextView mTvTitle;
    private ProgressBar mProgressBar;
    private DownLoadService mDownLoadService;
    private boolean mMustUpdate;
    private OnFragmentOperation mOnFragmentOperation;

    public static DownLoadDialog newInstance(String downLoadUrl, int notificationIcon) {

        Bundle args = new Bundle();
        args.putString(Constant.URL, downLoadUrl);
        args.putInt(Constant.NOTIFICATION_ICON, notificationIcon);
        DownLoadDialog fragment = new DownLoadDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static DownLoadDialog newInstance(String downLoadUrl, int notificationIcon, boolean mustUpdate) {

        Bundle args = new Bundle();
        args.putString(Constant.URL, downLoadUrl);
        args.putInt(Constant.NOTIFICATION_ICON, notificationIcon);
        args.putBoolean(Constant.MUST_UPDATE, mustUpdate);
        DownLoadDialog fragment = new DownLoadDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        mDownloadUrl = getArguments().getString(Constant.URL);
        notificationIcon = getArguments().getInt(Constant.NOTIFICATION_ICON);
        mMustUpdate = getArguments().getBoolean(Constant.MUST_UPDATE);
        if (mMustUpdate) {
            setCancelable(false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();
        mActivity = getActivity();
        mTvTitle = (TextView) view.findViewById(R.id.title);
        mBtnCancel = (Button) view.findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(this);
        mBtnBackground = (Button) view.findViewById(R.id.btnBackground);
        mBtnBackground.setOnClickListener(this);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setMax(100);

        Intent intent = new Intent(mContext, DownLoadService.class);
        mContext.bindService(intent, mConnection , Context.BIND_AUTO_CREATE);

        if (mMustUpdate) {
            view.findViewById(R.id.downLayout).setVisibility(View.GONE);
        }
        mOnFragmentOperation = (OnFragmentOperation) mContext;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnFragmentOperation = null;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownLoadService.DownLoadBinder binder = (DownLoadService.DownLoadBinder) service;
            mDownLoadService = binder.getService();
            mDownLoadService.registerProgressListener(mProgressListener);
            mDownLoadService.startDownLoad(mDownloadUrl);
            mDownLoadService.setNotificationIcon(notificationIcon);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDownLoadService = null;
        }
    };

    private DownLoadTask.ProgressListener mProgressListener = new DownLoadTask.ProgressListener() {
        @Override
        public void done() {
            mHandler.sendEmptyMessage(DONE);
        }

        @Override
        public void update(long bytesRead, long contentLength) {
            currentProgress = (int) (bytesRead * 100 / contentLength);
            if (currentProgress < 1) {
                currentProgress = 1;
            }
            Log.d(TAG, "" + bytesRead + "," + contentLength + ";current=" + currentProgress);
            Message message = mHandler.obtainMessage();
            message.what = LOADING;
            message.arg1 = currentProgress;
            Bundle bundle = new Bundle();
            bundle.putLong("bytesRead", bytesRead);
            bundle.putLong("contentLength", contentLength);
            message.setData(bundle);
            message.sendToTarget();
        }

        @Override
        public void onError() {
            mHandler.sendEmptyMessage(ERROE);
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnCancel) {
            doCancel();
        } else if (id == R.id.btnBackground) {
            doBackground();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unbindService(mConnection);
    }

    private void doCancel() {
        mDownLoadService.cancel();
        if (mActivity != null) {
            mActivity.finish();
            ToastUtils.show(mContext, R.string.update_lib_download_cancel);
        }
    }

    private void doBackground() {
        mDownLoadService.setBackground(true);
        mDownLoadService.showNotification(currentProgress);
        if (mActivity != null) {
            ToastUtils.show(mContext, R.string.update_lib_download_in_background);
            mActivity.finish();
        }

    }

    private final static int LOADING = 1000;
    private final static int DONE = 1001;
    private final static int ERROE = 1002;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    mProgressBar.setProgress(msg.arg1);
                    Bundle bundle = msg.getData();
                    long bytesRead = bundle.getLong("bytesRead");
                    long contentLength = bundle.getLong("contentLength");
                    mTvTitle.setText(String.format(getResources().getString(R.string.update_lib_file_download_format),
                                                   Formatter.formatFileSize(mActivity.getApplication(), bytesRead),
                                                   Formatter.formatFileSize(mActivity.getApplication(), contentLength)));
                    break;
                case DONE:
                    mContext.startActivity(FileMe.openApkFile(getActivity(), new File(FileMe.getApkFilePath(mActivity, mDownloadUrl))));
                    mActivity.finish();
                    ToastUtils.show(mContext, R.string.update_lib_download_finish);
                    break;
                case ERROE:

                    ToastUtils.show(mContext, R.string.update_lib_download_failed);
                    if (!mMustUpdate) {
                        dismiss();
                        mActivity.finish();
                    } else {
                        dismiss();
                        if (mOnFragmentOperation != null) {
                            mOnFragmentOperation.onFailed();
                        }
                    }

                    break;
            }
        }
    };

    public interface OnFragmentOperation {
        void onFailed();
    }

}
