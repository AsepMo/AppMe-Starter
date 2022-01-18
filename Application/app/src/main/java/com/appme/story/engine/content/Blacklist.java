package com.appme.story.engine.content;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.os.Bundle;


/**
 * Stores a (lazily built) list of all of the app's content providers that are not SafeContentResolver-whitelisted.
 */
class Blacklist {
    private static final String META_DATA_KEY_ALLOW_INTERNAL_ACCESS = "com.appme.story.engine.content.ALLOW_INTERNAL_ACCESS";


    private final Context context;
    private Set<String> blacklistedAuthorities;


    Blacklist(Context context) {
        this.context = context;
    }

    synchronized boolean isBlacklisted(String authority) {
        if (blacklistedAuthorities == null) {
            blacklistedAuthorities = findBlacklistedContentProviderAuthorities();
        }

        return blacklistedAuthorities.contains(authority);
    }

    private Set<String> findBlacklistedContentProviderAuthorities() {
        ProviderInfo[] providers = getProviderInfo(context);

        Set<String> blacklistedAuthorities = new HashSet<>(providers.length);
        for (ProviderInfo providerInfo : providers) {
            if (!isContentProviderWhitelisted(providerInfo)) {
                String[] authorities = providerInfo.authority.split(";");
                Collections.addAll(blacklistedAuthorities, authorities);
            }
        }

        return blacklistedAuthorities;
    }

    private ProviderInfo[] getProviderInfo(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_PROVIDERS | PackageManager.GET_META_DATA);

            ProviderInfo[] providers = packageInfo.providers;
            return providers != null ? providers : new ProviderInfo[0];
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isContentProviderWhitelisted(ProviderInfo providerInfo) {
        Bundle metaData = providerInfo.metaData;
        return metaData != null && metaData.getBoolean(META_DATA_KEY_ALLOW_INTERNAL_ACCESS, false);
    }
}
