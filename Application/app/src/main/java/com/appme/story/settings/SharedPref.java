package com.appme.story.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Comparator;

import com.appme.story.AppController;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.folders.preview.MimeTypes;
import com.appme.story.engine.app.folders.FileMe;
import com.stericson.RootTools.RootTools;

public class SharedPref {
    
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    public static final String SD_CARD_ROOT = FolderMe.FOLDER;
    public static final String EXTENSION = FileMe.MP4;
    private static final String 
    NAME = "FileExplorerPreferences",

    PREF_START_FOLDER = "start_folder",
    PREF_CARD_LAYOUT = "card_layout",
    PREF_SORT_BY = "sort_by";

    public static final int
    SORT_BY_NAME = 0,
    SORT_BY_TYPE = 1,
    SORT_BY_SIZE = 2;

    private static String PREF_HIGH_QUALITY = "pref_high_quality";
    private final static int DEFAULT_SORT_BY = SORT_BY_NAME;

    File startFolder;
    int sortBy;
    
    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(AppController.getContext()).edit();
    }
    
    public void setFirstTimeLaunch(boolean isFirstTime) {
        getEditor(context).putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).commit();
    }

    public boolean isFirstTimeLaunch() {
        return getPrefs(context).getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
   
    // Getter Methods
    public static String getWorkingFolder() {
        return getPrefs(AppController.getContext()).getString("working_folder", SD_CARD_ROOT);
    }
    
    public static void setWorkingFolder(String value) {
        getEditor(AppController.getContext()).putString("working_folder", value).commit();
    }
     
    public static void setWorkingFile(String value) {
        getEditor(AppController.getContext()).putString("working_file", value).commit();
    }

    public static String getWorkingFile() {
        return getPrefs(AppController.getContext()).getString("working_file", EXTENSION);
    }
    
    public static String[] getSavedPaths() {
        return getPrefs(AppController.getContext()).getString("savedPaths", "").split(",");
    }
      
    public static void setSavedPaths(StringBuilder stringBuilder) {
        getEditor(AppController.getContext()).putString("savedPaths", stringBuilder.toString()).commit();
    }

    public static boolean showThumbnail() {
        return getPrefs(AppController.getContext()).getBoolean("showpreview", true);
    }

    public static boolean showHiddenFiles() {
        return getPrefs(AppController.getContext()).getBoolean("displayhiddenfiles", true);
    }

    public static boolean rootAccess() {
        return getPrefs(AppController.getContext()).getBoolean("enablerootaccess", false) && RootTools.isAccessGiven();
    }
    
    public static SharedPref loadPreferences(Context context)
    {
        SharedPref instance = new SharedPref(context);
        instance.showThumbnail();
        instance.showHiddenFiles();
        instance.rootAccess();
        instance.setFirstTimeLaunch(true);
        instance.loadFromSharedPreferences(context.getSharedPreferences(NAME, Context.MODE_PRIVATE));
        return instance;
    }
    
    private void loadFromSharedPreferences(SharedPreferences sharedPreferences)
    {
        String startPath = sharedPreferences.getString(PREF_START_FOLDER, null);
        if (startPath == null)
        {
            if (Environment.getExternalStorageDirectory().list() != null)
                startFolder = Environment.getExternalStorageDirectory();
            else 
                startFolder = new File("/");
        }
        else this.startFolder = new File(startPath);
        this.sortBy = sharedPreferences.getInt(PREF_SORT_BY, DEFAULT_SORT_BY);
    }

    private void saveToSharedPreferences(SharedPreferences sharedPreferences)
    {
        sharedPreferences.edit()
            .putString(PREF_START_FOLDER, startFolder.getAbsolutePath())
            .putInt(PREF_SORT_BY, sortBy)
            .apply();
    }

    public void saveChangesAsync(final Context context)
    {
        new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    saveChanges(context);

                }
            }).run();
    }

    public void saveChanges(Context context)
    {
        saveToSharedPreferences(context.getSharedPreferences(NAME, Context.MODE_PRIVATE));
    }


    public int getSortBy()
    {
        return sortBy;
    }

    public File getStartFolder()
    {
        if (startFolder.exists() == false)
            startFolder = new File("/");
        return startFolder;
    }

    public Comparator<File> getFileSortingComparator()
    {
        switch (sortBy)
        {
            case SORT_BY_SIZE:
                return new FileMe.FileSizeComparator();

            case SORT_BY_TYPE:
                return new FileMe.FileExtensionComparator();

            default:
                return new FileMe.FileNameComparator();
        }
    }

    public static void setPrefHighQuality(Context context, boolean isEnabled) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_HIGH_QUALITY, isEnabled);
        editor.apply();
    }

    public static boolean getPrefHighQuality(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREF_HIGH_QUALITY, false);
    }
}
