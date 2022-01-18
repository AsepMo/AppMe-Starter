package com.appme.story.engine.content;

import android.content.Context;

import com.getkeepsafe.relinker.MissingLibraryException;
import com.getkeepsafe.relinker.ReLinker;


class Os {
    private static final String LIBRARY_NAME = "os-compat";

    private static Context context;
    private static boolean libraryNeedsLoading = true;
    private static UnsupportedOperationException loadFailedException;


    synchronized static void init(Context context) {
        if (context == null) {
            throw new NullPointerException("Argument 'context' must not be null");
        }

        // Only get the context here. Load the library before doing the actual work (hopefully in a background thread).
        if (Os.context == null) {
            Os.context = context.getApplicationContext();
        }
    }

    static int fstat(int fileDescriptor) throws ErrnoException, UnsupportedOperationException {
        synchronized (Os.class) {
            if (context == null) {
                throw new IllegalStateException("Call Os.init(Context) before attempting to call Os.fstat()");
            }

            if (libraryNeedsLoading) {
                loadLibrary();
            } else if (loadFailedException != null) {
                throw loadFailedException;
            }
        }

        return nativeFstat(fileDescriptor);
    }

    private static void loadLibrary() {
        libraryNeedsLoading = false;
        try {
            ReLinker.loadLibrary(context, LIBRARY_NAME);
        } catch (MissingLibraryException | UnsatisfiedLinkError e) {
            loadFailedException = new UnsupportedOperationException("Failed to load native library " + LIBRARY_NAME, e);
            throw loadFailedException;
        }
    }

    private static native int nativeFstat(int fileDescriptor) throws ErrnoException;
}
