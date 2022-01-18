package com.appme.story.engine.content;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

public class SampleContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.appme.story.engine.content";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        AssetManager assetManager = getContext().getAssets();
        try {
            return assetManager.openFd("sample.txt");
        } catch (IOException e) {
            throw new FileNotFoundException("Error: " + e.getMessage());
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
