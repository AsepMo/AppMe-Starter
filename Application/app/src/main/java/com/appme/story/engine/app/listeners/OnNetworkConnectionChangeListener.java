package com.appme.story.engine.app.listeners;

import android.content.Context;

public interface OnNetworkConnectionChangeListener {
    void onConnected();

    void onDisconnected();

    /**
     * @return current context for register all components in the same context
     * and notify connectivity listener
     */
    Context getContext();
}
