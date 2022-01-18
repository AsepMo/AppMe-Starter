package com.appme.story.engine.app.folders.root.listeners;

public interface OnCommandListener {

    public void onSuccess(final String msg);
    
    public void onError(final String msg);

    public void onError(final Exception exc);

    public void onNotRoot();
    
    public void onStatus(final int status);
    
}
