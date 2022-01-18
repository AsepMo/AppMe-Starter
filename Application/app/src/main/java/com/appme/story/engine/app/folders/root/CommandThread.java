package com.appme.story.engine.app.folders.root;

import java.io.IOException;

import android.util.Log;
import com.appme.story.engine.app.folders.root.command.ShellUtils;

public class CommandThread extends Thread {

    private static final String TAG = CommandThread.class.getSimpleName();

    private OnCommandListener onCommandListener;
    private String[] command;
    public CommandThread(String[] command) { 
        this.command = command;
    }

    
    @Override
    public void run() {
        Log.d(TAG, "Executing script shell command...");
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;

        try {
            try {

                proc = runtime.exec(command);
                
            } catch (IOException e) {
                logFailure(e);
                if (e.getMessage().contains("Error running exec(). Command: [su")) {
                    onCommandListener.onNotRoot();
                } else {
                    onCommandListener.onError(e);
                }
            } catch (Exception e) {
                logFailure(e);
                onCommandListener.onError(e);
            }
            try {
                if (proc != null) {
                    Thread aliveCheck = new AliveCheckThread(proc, this, onCommandListener);
                    aliveCheck.start();
                                      
                    Log.i(TAG, "Waiting for shutdown...");
                    int exitCode = proc.waitFor();
                    if(onCommandListener != null){
                        onCommandListener.onStatus(exitCode);
                    }
                    
                    Log.i(TAG, "Process exited with code " + exitCode + ".");
                    aliveCheck.interrupt();
                }
            } catch (InterruptedException e) {
                logFailure(e);
                Log.e(TAG, "Interrupted while waiting for process to finish.");
            }
            if (proc != null) {
                String stdErr = ShellUtils.dumpProcessOutput(proc, onCommandListener);
                if (proc.exitValue() != 0 && stdErr.length() > 0) {
                    logFailure();
                    if (stdErr.contains("not allowed to su")) {
                        onCommandListener.onNotRoot();
                    } else {
                        onCommandListener.onError(stdErr);
                    }
                }
            }
        } finally {
            // Clean up
            if (proc != null) {
                proc.destroy();
            }
        }
    }

    private void logFailure() {
        logFailure(null);
    }

    private void logFailure(Exception e) {
        Log.e(TAG, "Failed to execute scipt shell command.", e);
    }
	
	public void setCommandThreadListener(OnCommandListener listener){
		if (onCommandListener == null) {
            throw new IllegalArgumentException("errorListener cannot be null.");
        }
		this.onCommandListener = listener;
	}
	
	public interface OnCommandListener {

		public void onSuccess(final String msg);

		public void onError(final String msg);

		public void onError(final Exception exc);

		public void onNotRoot();

		public void onStatus(final int status);

	}
}
