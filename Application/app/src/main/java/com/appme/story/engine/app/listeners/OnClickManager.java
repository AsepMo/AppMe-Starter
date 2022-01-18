package com.appme.story.engine.app.listeners;

import android.annotation.TargetApi;
import android.support.v4.app.ShareCompat;
import android.support.v4.provider.DocumentFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.appme.story.R;
import com.appme.story.engine.app.folders.preview.MimeTypes;
import com.appme.story.engine.app.tasks.TheTask;

public class OnClickManager {
    
    public static Intent createFileOpenIntent(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);     
        intent.setDataAndType(Uri.fromFile(file), MimeTypes.getMimeType(file));
        return intent;
    }

    public static void openFile(final Context c, final File file) {
        new TheTask(c, file.getName(), new Runnable() {
                @Override
                public void run() {

                    if (file.isDirectory())
                        throw new IllegalArgumentException("File cannot be a directory!");

                    Intent intent = createFileOpenIntent(file);

                    try {
                        c.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        c.startActivity(Intent.createChooser(intent, c.getString(R.string.action_open_file, file.getName())));
                    } catch (Exception e) {
                        new AlertDialog.Builder(c)
                            .setMessage(e.getMessage())
                            .setTitle(R.string.action_open_cant_openfile)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                    }
                }
            }).execute();   
    }

    public static void shareFile(Activity c, String path) {
        File file = new File(path);
        Intent shareIntent = ShareCompat.IntentBuilder.from(c)
            .addStream(Uri.fromFile(file))
            .setType(MimeTypes.getMimeType(file))
            .getIntent();

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String title = c.getString(R.string.action_share, file.getName());
        if (shareIntent.resolveActivity(c.getPackageManager()) != null) {
            c.startActivity(Intent.createChooser(shareIntent, title));
        } else {
            String error = c.getString(R.string.action_share_failed, file.getName());
            Toast.makeText(c, error, Toast.LENGTH_SHORT).show();
        }
    }
}
