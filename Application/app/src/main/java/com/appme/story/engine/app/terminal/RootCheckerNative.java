package com.appme.story.engine.app.terminal;

import com.appme.story.engine.app.utils.QLog;

public class RootCheckerNative {

    private static boolean libraryLoaded = false;

    /**
     * Loads the C/C++ libraries statically
     */
    static {
        try {
            System.loadLibrary("toolChecker");
            libraryLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            QLog.e(e);
        }
    }

    public boolean wasNativeLibraryLoaded() {
        return libraryLoaded;
    }

    public native int checkForRoot(Object[] pathArray);

    public native int setLogDebugMessages(boolean logDebugMessages);

}
