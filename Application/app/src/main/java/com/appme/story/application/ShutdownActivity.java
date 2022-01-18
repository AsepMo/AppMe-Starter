package com.appme.story.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

import com.appme.story.R;
import com.appme.story.engine.app.folders.root.CommandThread;
import com.appme.story.engine.app.folders.root.listeners.OnCommandListener;
import com.appme.story.engine.app.folders.root.command.ShellUtils;

public class ShutdownActivity extends Activity implements DialogInterface.OnKeyListener {

    public static final String TAG = ShutdownActivity.class.getSimpleName();
    public static void startCommand(Context c) {
        Intent mApplication = new Intent(c, ShutdownActivity.class);
        c.startActivity(mApplication);
    }

	//private CommandThread mCommandThread;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.appme_run_shell).setOnKeyListener(this).setCancelable(true)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    ShutdownActivity.this.setCommand("ls /sdcard/");
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    ShutdownActivity.this.forceExit();
                }
            });
        AlertDialog alert = builder.create();
        alert.show();*/
    }

    /*public void setCommand(String command) {
        mCommandThread = new CommandThread(new String[]{"sh", "-c", command});
		mCommandThread.setCommandThreadListener(this);
		mCommandThread.start();
    }
    
    public void setCommandRoot(String command) {
        mCommandThread = new CommandThread(new String[]{"su", "-c", command});
		mCommandThread.setCommandThreadListener(this);
		mCommandThread.start();
}
    
    @Override
    public void onSuccess(final String msg) {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShutdownActivity.this);
                    builder.setMessage(msg + "\n").setOnKeyListener(new DialogInterface.OnKeyListener(){
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    dialog.dismiss();
                                    forceExit();
                                    return true;
                                }
                                return false;
                            }
                            
                    }).setCancelable(true)
                        .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
    }

    @Override
    public void onNotRoot() {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showNotRootedDialog();
                }
            });
    }

    @Override
    public void onError(final String msg) {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorDialog(msg);
                }
            });
    }

    @Override
    public void onError(final Exception exc) {
        final String msg = exc.getClass().getSimpleName() + ": " + exc.getMessage();
        onError(msg);
    }

    @Override
    public void onStatus(final int status) {
        
    }

    private void showErrorDialog(String msg) {
        AlertDialog.Builder builder = buildErrorDialog(msg);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showNotRootedDialog() {
        final Uri uri = Uri.parse(getString(R.string.appme_rooting_url));
        AlertDialog.Builder builder = buildErrorDialog(getString(R.string.appme_not_rooted));
        builder.setNegativeButton(R.string.appme_what_root, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    ShutdownActivity.this.forceExit();
                }
            });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private AlertDialog.Builder buildErrorDialog(String msg) {
        return new AlertDialog.Builder(this).setMessage(msg).setOnKeyListener(this).setCancelable(true)
            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    ShutdownActivity.this.forceExit();
                }
            });
    }*/

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialog.dismiss();
            forceExit();
            return true;
        }
        return false;
    }

    private void forceExit() {
        finish();
        ShellUtils.killMyProcess();
	}
}
