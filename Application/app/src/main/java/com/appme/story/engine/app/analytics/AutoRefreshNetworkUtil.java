package com.appme.story.engine.app.analytics;

public final class AutoRefreshNetworkUtil {
    private AutoRefreshNetworkUtil() {
    }

    public static void removeAllRegisterNetworkListener() {
        CheckNetworkConnectionHelper
                .getInstance()
                .unregisterNetworkChangeListener();
    }
}
