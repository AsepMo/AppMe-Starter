package com.appme.story.application;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.IOException;

import okio.BufferedSink;
import okio.Okio;
import com.appme.story.engine.app.folders.FolderMe;

public class ApplicationMain extends Application {
    
    public static final String TAG = "ApplicationMain";
    private static final String APPME_FILE_NAME = "appMe.dat";
    
    private static volatile Context mContext;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mContext = this;
        
        initAnalytics();
        initCrashHandler();
        initConfig();
        initFolder();
        initSoundManager();
    }
    
    public static synchronized Context getContext(){
        return mContext;
    }
    
    public void initAnalytics(){}
    public void initCrashHandler(){}
    public void initConfig(){
        createInternalFileIfNecessary();
    }
    public void initFolder(){}
    public void initSoundManager(){}
    
    private void createInternalFileIfNecessary() {
        File internalFile = getInternalFile(getContext());
		internalFile.getParentFile().mkdirs();
        if (!internalFile.exists()) {
            createFile(internalFile);
            writeSecretFile(internalFile);
        }
    }

    private void createFile(File appMeFile) {
        try {
            boolean success = appMeFile.createNewFile();
            if (!success) {
                throw new RuntimeException("File wasn't created");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating file", e);
        }
    }

    private void writeSecretFile(File internalFile) {
        try {
            BufferedSink bufferedSink = Okio.buffer(Okio.sink(internalFile));
            try {
                bufferedSink.writeUtf8("secret");
            } finally {
                bufferedSink.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing file", e);
        }
    }

    public static File getInternalFile(Context context) {
        File filesDir = new File(context.getExternalFilesDir(null).getAbsolutePath());
        return new File(filesDir, APPME_FILE_NAME);
    }
}
