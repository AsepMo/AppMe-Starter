package com.appme.story.settings;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Locale;

import com.appme.story.R;

public class PrefStore {
    
    public static final String APP_PREF_NAME = "app_settings";
    public static final String LOG_FILE = "busybox.log";

    public static final String FILE_PATH = "last_file";
    @NonNull
    protected SharedPreferences.Editor editor;
    @NonNull
    protected Context context;
    @NonNull
    private SharedPreferences sharedPreferences;

    @SuppressLint("CommitPrefEdits")
    public PrefStore(@NonNull Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public PrefStore(@NonNull SharedPreferences mPreferences, @NonNull Context context) {
        this.context = context;
        this.sharedPreferences = mPreferences;
        this.editor = sharedPreferences.edit();
    }

    @NonNull
    public Context getContext() {
        return context;
    }

    @NonNull
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(@NonNull SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void put(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void put(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public int getInt(String key, int def) {
        try {
            return sharedPreferences.getInt(key, def);
        } catch (Exception e) {
            try {
                return Integer.parseInt(getString(key));
            } catch (Exception ignored) {
                return def;
            }
        }
    }

    /**
     * get long value from key,
     *
     * @param key - key
     * @return -1 if not found
     */
    public long getLong(String key) {
        try {
            return sharedPreferences.getLong(key, -1);
        } catch (Exception e) {
            try {
                return Long.parseLong(getString(key));
            } catch (Exception ignored) {
            }
        }
        return -1;
    }

    public String getString(String key) {
        String s = "";
        try {
            s = sharedPreferences.getString(key, "");
        } catch (Exception ignored) {
        }
        return s;
    }

    public boolean getBoolean(String key) {
        try {
            return sharedPreferences.getBoolean(key, false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean getBoolean(String key, boolean def) {
        try {
            return sharedPreferences.getBoolean(key, def);
        } catch (Exception e) {
            return def;
        }
    }

    public boolean useFullScreen() {
        return getBoolean(context.getString(R.string.key_full_screen));
    }

    public int getConsoleBackground() {
//        return getInt(mContext.readBuffer(R.string.key_bg_console));
        return Color.BLACK;
    }

    public int getConsoleTextColor() {
//        return getInt(mContext.readBuffer(R.string.key_console_text_color));
        return Color.WHITE;
    } 

    public boolean hasSystemInstalled() {
        return getBoolean("system_installed");
    }

    public String getSystemVersion() {
        String version = getString("system_version");
        return version.isEmpty() ? "Unknown" : version;
    }

    public boolean installViaRootAccess() {
        return getBoolean(context.getString(R.string.key_install_root), false);
    }

    public boolean showThumbnail() {
        return sharedPreferences.getBoolean("showpreview", true);
    }
    
    public boolean showHiddenFiles() {
        return sharedPreferences.getBoolean("displayhiddenfiles", true);
    }
    
    /**
     * Get application version
     *
     * @param c context
     * @return version, format versionName-versionCode
     */
    public static String getVersion(Context c) {
        String version = "";
        try {
            PackageInfo pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            version = pi.versionName + "-" + pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * Get external storage path
     *
     * @return path
     */
    public static String getStorage() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * Get environment directory
     *
     * @param c context
     * @return path, e.g. /data/data/com.example.app/files
     */
    public static String getFolderMe(Context c) {
        return c.getFilesDir().getAbsolutePath();
    }

    /**
     * Get language code
     *
     * @param c context
     * @return locale
     */
    public static Locale getLocale(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String language = pref.getString("language", c.getString(R.string.language));
        boolean emptyLang = language.isEmpty();
        if (emptyLang) {
            language = Locale.getDefault().getLanguage();
        }
        Locale locale;
        switch (language.toLowerCase()) {
            case "id":
            case "ja":
            case "es":
            case "fr":
            case "ko":
            case "pl":
            case "ru":
            case "uk":
                locale = new Locale(language);
                break;
            case "zh_cn":
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case "zh_tw":
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            default:
                language = "en";
                locale = Locale.ENGLISH;
        }
        if (emptyLang) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("language", language);
            editor.apply();
        }
        return locale;
    }

    /**
     * Get application theme resource id
     *
     * @param c context
     * @return resource id
     */
    public static int getTheme(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String theme = pref.getString("theme", c.getString(R.string.theme));
        int themeId = R.style.AppTheme;
        switch (theme) {
            case "dark":
                themeId = R.style.AppTheme;
                break;
            case "light":
                themeId = R.style.AppTheme;
                break;
        }
        return themeId;
    }

    /**
     * Get font size
     *
     * @param c context
     * @return font size
     */
    public static int getFontSize(Context c) {
        int fontSizeInt;
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String fontSize = pref.getString("fontsize", c.getString(R.string.fontsize));
        try {
            fontSizeInt = Integer.parseInt(fontSize);
        } catch (Exception e) {
            fontSize = c.getString(R.string.fontsize);
            fontSizeInt = Integer.parseInt(fontSize);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("fontsize", fontSize);
            editor.apply();
        }
        return fontSizeInt;
    }

    /**
     * Get maximum limit to scroll
     *
     * @param c context
     * @return number of lines
     */
    public static int getMaxLines(Context c) {
        int maxLinesInt;
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String maxLines = pref.getString("maxlines", c.getString(R.string.maxlines));
        try {
            maxLinesInt = Integer.parseInt(maxLines);
        } catch (Exception e) {
            maxLines = c.getString(R.string.maxlines);
            maxLinesInt = Integer.parseInt(maxLines);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("maxlines", maxLines);
            editor.apply();
        }
        return maxLinesInt;
    }

    /**
     * Timestamp is enabled
     *
     * @param c context
     * @return true if enabled
     */
    public static boolean isTimestamp(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("timestamp", c.getString(R.string.timestamp).equals("true"));
    }

    /**
     * Debug mode is enabled
     *
     * @param c context
     * @return true if enabled
     */
    public static boolean isDebugMode(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("debug", c.getString(R.string.debug).equals("true"));
    }

    /**
     * Trace mode is enabled
     *
     * @param c context
     * @return true if enabled
     */
    public static boolean isTraceMode(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("debug", c.getString(R.string.debug).equals("true")) &&
            pref.getBoolean("trace", c.getString(R.string.trace).equals("true"));
    }

    /**
     * Logging is enabled
     *
     * @param c context
     * @return true if enabled
     */
    public static boolean isLogger(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("logger", c.getString(R.string.logger).equals("true"));
    }

    /**
     * Get path of log file
     *
     * @param c context
     * @return path
     */
    public static String getLogFile(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String logFile = pref.getString("logfile", c.getString(R.string.logfile));
        if (!logFile.contains("/")) {
            logFile = getStorage() + "/" + LOG_FILE;
        }
        return logFile;
    }

    /**
     * Get hardware architecture
     *
     * @param arch unformated architecture
     * @return arm, arm_64, x86, x86_64
     */
    public static String getArch(String arch) {
        String march = "unknown";
        if (arch.length() > 0) {
            char a = arch.toLowerCase().charAt(0);
            switch (a) {
                case 'a':
                    if (arch.equals("amd64")) march = "x86_64";
                    else if (arch.contains("64")) march = "arm64";
                    else march = "arm";
                    break;
                case 'i':
                case 'x':
                    if (arch.contains("64")) march = "x86_64";
                    else march = "x86";
                    break;
                case 'm':
                    if (arch.contains("64")) march = "mips64";
                    else march = "mips";
                    break;
            }
        }
        return march;
    }

    /**
     * Get current hardware architecture
     *
     * @return arm, arm_64, x86, x86_64
     */
    public static String getArch() {
        return getArch(System.getProperty("os.arch"));
    }

    /**
     * Get directory of BusyBox installation
     *
     * @param c context
     * @return path, e.g. /system/xbin
     */
    public static String getInstallDir(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("installdir", c.getString(R.string.installdir));
    }

    /**
     * Applets is enabled
     *
     * @param c context
     * @return true, if install applets
     */
    public static boolean isInstallApplets(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("applets", c.getString(R.string.applets).equals("true"));
    }

    /**
     * Replace mode is enabled
     *
     * @param c context
     * @return true, if replace applets
     */
    public static boolean isReplaceApplets(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("replace", c.getString(R.string.replace).equals("true"));
    }

    /**
     * Mount as RAM disk
     *
     * @param c context
     * @return true, if ram disk
     */
    public static boolean isRamDisk(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("ramdisk", c.getString(R.string.ramdisk).equals("true"));
    }

    /**
     * Set application locale
     *
     * @param c context
     */
    public static void setLocale(Context c) {
        Locale locale = getLocale(c);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        c.getResources().updateConfiguration(config, c.getResources().getDisplayMetrics());
    }
    
    
}
