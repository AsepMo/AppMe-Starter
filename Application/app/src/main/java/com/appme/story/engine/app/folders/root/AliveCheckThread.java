package com.appme.story.engine.app.folders.root;

import android.util.Log;
import com.appme.story.engine.app.folders.root.command.ShellUtils;
import com.appme.story.engine.app.folders.root.CommandThread.OnCommandListener;

public class AliveCheckThread extends Thread {

    private final static String TAG = AliveCheckThread.class.getSimpleName();

    private final Process proc;
    private final CommandThread shutdownThread;
    private final OnCommandListener onCommandListener;
    
    public AliveCheckThread(Process proc, CommandThread shutdownThread, OnCommandListener onCommandListener) {
        this.proc = proc;
        this.shutdownThread = shutdownThread;
        this.onCommandListener = onCommandListener;
    }

    @Override
    public void run() {
        try {
            sleep(15000); // wait 15s, because Superuser also has 10s timeout
        } catch (InterruptedException e) {
            Log.i(TAG, "Interrupted.");
            return;
        }
        Log.w(TAG, "Still alive after 15 sec...");
        ShellUtils.dumpProcessOutput(proc, onCommandListener);
        proc.destroy();
        shutdownThread.interrupt();
        Log.w(TAG, "Interrupted and destroyed.");

        ShellUtils.killMyProcess();
    }
}
