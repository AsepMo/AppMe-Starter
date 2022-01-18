package com.appme.story.engine.app.listeners;

import com.appme.story.receiver.NetworkBroadcastReceiver;

public interface Subject {
    void registerObserver(OnNetworkConnectionChangeListener listener);

    void unregisterObserver(OnNetworkConnectionChangeListener listener);

    void notifyNetworkObserverChange(NetworkBroadcastReceiver.NetworkState networkState);
}
