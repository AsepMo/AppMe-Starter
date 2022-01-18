package com.appme.story.engine.app.folders;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.service.InstallService;
import com.appme.story.service.InstallServiceHelper;
import com.appme.story.settings.Settings;


public class AssetManager {
    
    private static final String TAG = AssetManager.class.getSimpleName();

    private static volatile AssetManager Instance = null;
    private Context context;
	private static final int MSG_ASSETS_COPY_SUCCESS = 100;
    private static final int MSG_ASSETS_COPY_FAILED = 101;
    private OnAssetsManagerListener mOnAssetsManagerListener;
	
    private OnStartActivityListener mOnStartActivityListener;
    public static AssetManager getInstance() {
        AssetManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (AssetManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new AssetManager(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private AssetManager(Context context) {
        this.context = context;         
    }

    public static AssetManager with(Context context) {
        return new AssetManager(context);
    }
	
	public AssetManager extractAssets(){
        // prepare env directory
        String envDir = AppController.getContext().getExternalFilesDir("all").getAbsolutePath();
        File fEnvDir = new File(envDir);
        fEnvDir.mkdirs();
        if (!fEnvDir.exists()) {

        }
        FileMe.cleanDirectory(fEnvDir);

        // extract assets
        if (!extractDir(AppController.getContext(), "all", "")) {
            mAssetHandler.obtainMessage(MSG_ASSETS_COPY_FAILED).sendToTarget();

        }else{
            mAssetHandler.obtainMessage(MSG_ASSETS_COPY_SUCCESS).sendToTarget();

        }
        return this;
    }
    
    private Handler mHandler;
    private Runnable mStartActivity = new Runnable(){
        @Override
        public void run(){
            if(mOnStartActivityListener != null){
                mOnStartActivityListener.onStart();
            }
        }
    };

    public void init()
    {
        mHandler = new Handler();
        mHandler.postDelayed(mStartActivity, 2000);
    }

    public void setStartActivity(OnStartActivityListener mOnStartActivityListener){
        this.mOnStartActivityListener = mOnStartActivityListener;
    }
    
    public void extract(InstallService installService) {
        installService.broadcastStatus("extract_assets_to_storage", "Extract Assets To Storage");                       
    }

     
    public static boolean isWebfileInstalled() {

        File fileDir = FolderMe.getInstance().getWebDir();
        File webFile = new File(fileDir, "web/index.html");
        Log.d(TAG, "check web file dir " + webFile.getAbsolutePath() + ",exist=" + webFile.exists());
        if (!webFile.exists()) {
            return false;
        }   
        String md5 = getWebFileMd5(AppController.getContext());
        return  Settings.getWebFileMd5().equals(md5);

    }

    public static void installWebfile(Handler handler) {
        
        String folderPath = FolderMe.getInstance().getWebDir().getAbsolutePath() + "/";
        InputStream is = AppController.getContext().getResources().openRawResource(R.raw.web);
        if (unzip(is, folderPath, handler)) {
            String md5 = getWebFileMd5(AppController.getContext());
            Settings.setWebFileMd5(md5);
        }
    }
    

    private static String getWebFileMd5(Context context) {

        MessageDigest digest = null;
        InputStream in = context.getResources().openRawResource(R.raw.web);
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);

    }

    public static boolean unzip(InputStream zipFileName, String outputDirectory, Handler handler) {
                                 
        try {
            ZipInputStream in = new ZipInputStream(zipFileName);

            ZipEntry entry = in.getNextEntry();
            while (entry != null) {

                File file = new File(outputDirectory);
                if (!file.exists()) {
                    file.mkdir();
                }

                if (entry.isDirectory()) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - 1);

                    file = new File(outputDirectory + File.separator + name);
                    file.mkdir();

                } else {
                    file = new File(outputDirectory + File.separator
                                    + entry.getName());
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    int b;
                    while ((b = in.read()) != -1) {
                        out.write(b);
                    }
                    out.close();
                }
                entry = in.getNextEntry();
            }
            in.close();


            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
            return false;
        }
    }
    
    public interface OnStartActivityListener {
        void onStart(); 
    }

    public void setOnAssetsManagerListener(OnAssetsManagerListener mOnAssetsManagerListener){
        this.mOnAssetsManagerListener = mOnAssetsManagerListener;
    }

    public interface OnAssetsManagerListener {
        void onSuccess(String path);
        void onFail(String message);
    }

    private Handler mAssetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ASSETS_COPY_SUCCESS: {
						// Toast.makeText(AppController.getContext(), "Extract Success", Toast.LENGTH_SHORT).show();
                        if(mOnAssetsManagerListener != null){
                            mOnAssetsManagerListener.onSuccess("");
                        }
                        break;
                    }
                case MSG_ASSETS_COPY_FAILED: {
                        if(mOnAssetsManagerListener != null){
                            mOnAssetsManagerListener.onFail("File Not Found");
                        }
						// Toast.makeText(AppController.getContext(), "Extract Failed", Toast.LENGTH_SHORT).show();
                        break;
                    }
            }
        }
    };
    /**
     * Extract file to env directory
     *
     * @param c         context
     * @param rootAsset root asset name
     * @param path      path to asset file
     * @return false if error
     */
    private boolean extractFile(Context c, String rootAsset, String path) {
        android.content.res.AssetManager assetManager = c.getAssets();
        InputStream in = null;
        OutputStream out = null;
        //boolean success = false;
        try {
            in = assetManager.open(rootAsset + path);
            String fullPath = FolderMe.getInstance().getExternalFileDir(rootAsset) + path;
            out = new FileOutputStream(fullPath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
			//  success = true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(in);
            close(out);
        }
        /*if (success) {
		 mHandler.obtainMessage(MSG_APK_COPY_SUCCESS).sendToTarget();
		 } else {
		 mHandler.obtainMessage(MSG_APK_COPY_FAILED).sendToTarget();
		 }*/
        return true;
    }

    /**
     * Extract path to env directory
     *
     * @param c         context
     * @param rootAsset root asset name
     * @param path      path to asset directory
     * @return false if error
     */
    private boolean extractDir(Context c, String rootAsset, String path) {
        android.content.res.AssetManager assetManager = c.getAssets();
        try {
            String[] assets = assetManager.list(rootAsset + path);
            if (assets.length == 0) {
                if (!extractFile(c, rootAsset, path)) return false;
            } else {
                String fullPath = FolderMe.getInstance().getExternalFileDir(rootAsset) + path;
                File dir = new File(fullPath);
                if (!dir.exists()) dir.mkdir();
                for (String asset : assets) {
                    if (!extractDir(c, rootAsset, path + "/" + asset)) return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Recursive set permissions to directory
     *
     * @param path path to directory
     */
    private static void setPermissions(File path) {
        if (path == null) return;
        if (path.exists()) {
            path.setReadable(true, false);
            path.setExecutable(true, false);
            File[] list = path.listFiles();
            if (list == null) return;
            for (File f : list) {
                if (f.isDirectory()) setPermissions(f);
                f.setReadable(true, false);
                f.setExecutable(true, false);
            }
        }
    }

	/**
     * Closeable helper
     *
     * @param c closable object
     */
    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
