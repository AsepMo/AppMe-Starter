package com.appme.story.service.server;

import com.appme.story.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ServerManager extends BroadcastReceiver {

    private static final String ACTION = "com.appme.webserver.receiver";

    private static final String CMD_KEY = "CMD_KEY";
    private static final String MESSAGE_KEY = "MESSAGE_KEY";

    private static final int CMD_VALUE_START = 1;
    private static final int CMD_VALUE_ERROR = 2;
    private static final int CMD_VALUE_STOP = 4;

    /**
     * Notify serverStart.
     *
     * @param context context.
     */
    public static void serverStart(Context context, String hostAddress) {
        sendBroadcast(context, CMD_VALUE_START, hostAddress);
    }

    /**
     * Notify serverStop.
     *
     * @param context context.
     */
    public static void serverError(Context context, String error) {
        sendBroadcast(context, CMD_VALUE_ERROR, error);
    }

    /**
     * Notify serverStop.
     *
     * @param context context.
     */
    public static void serverStop(Context context) {
        sendBroadcast(context, CMD_VALUE_STOP);
    }

    private static void sendBroadcast(Context context, int cmd) {
        sendBroadcast(context, cmd, null);
    }

    private static void sendBroadcast(Context context, int cmd, String message) {
        Intent broadcast = new Intent(ACTION);
        broadcast.putExtra(CMD_KEY, cmd);
        broadcast.putExtra(MESSAGE_KEY, message);
        context.sendBroadcast(broadcast);
    }

    private Activity mActivity;
    private Intent mService;
    private OnServerManagerListener mListener;
    public ServerManager(Activity activity) {
        this.mActivity = activity;
        mService = new Intent(activity, CoreService.class);
    }

    /**
     * Register broadcast.
     */
    public void register() {
        IntentFilter filter = new IntentFilter(ACTION);
        mActivity.registerReceiver(this, filter);
    }

    public void startService() {
        mActivity.startService(mService);
    }

    public void stopService() {
        mActivity.stopService(mService);
    }

    /**
     * UnRegister broadcast.
     */
    public void unRegister() {
        mActivity.unregisterReceiver(this);
    }
    
    public void setOnServerManagerListener(OnServerManagerListener mListener){
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION.equals(action)) {
            int cmd = intent.getIntExtra(CMD_KEY, 0);
            switch (cmd) {
                case CMD_VALUE_START: {
                    String ip = intent.getStringExtra(MESSAGE_KEY);
                    mListener.onServerStart(ip);
                    break;
                }
                case CMD_VALUE_ERROR: {
                    String error = intent.getStringExtra(MESSAGE_KEY);
                    mListener.onServerError(error);
                    break;
                }
                case CMD_VALUE_STOP: {
                    mListener.onServerStop();
                    break;
                }
            }
        }
    }

    public interface OnServerManagerListener {
        void onServerStart(String ipAddress);
        void onServerError(String message);
        void onServerStop();
    }
}
