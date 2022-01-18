package com.appme.story.engine.content;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;


/**
 * Helper for creating a {@code SafeContentResolver} instance.
 *
 * <p>
 * The functionality to retrieve the User ID that owns a file was added to the framework in API 21. On versions prior
 * to that this library uses a small native code module to ask the operating system for the UID.
 * </p><p>
 * This helper takes care of instantiating the appropriate implementation for the Android version the app is running
 * on.
 * </p>
 */
public final class SafeContentResolverCompat {

    /**
     * Create a {@code SafeContentResolver} instance appropriate for the Android version the app is running on.
     *
     * @param context
     *         {@link Context} used to retrieve a {@link ContentResolver} instance and the list of content providers
     *         of this application.
     */
    @NonNull
    public static SafeContentResolver newInstance(@NonNull Context context) {
        //noinspection ConstantConditions
        if (context == null) {
            throw new NullPointerException("Argument 'context' must not be null");
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return new SafeContentResolverApi14(context);
        } else {
            return new SafeContentResolverApi21(context);
        }
    }
}
