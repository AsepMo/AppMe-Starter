package com.appme.story.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appme.story.service.ServiceUtils;


public class OnBootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            ServiceUtils.getInstance().launchAppMeService();
        }

    }

}

