package com.appme.story.engine.content;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

import android.content.Context;
import android.support.annotation.NonNull;
import android.system.Os;
import android.system.StructStat;


final class SafeContentResolverApi21 extends SafeContentResolver {
    SafeContentResolverApi21(Context context) {
        super(context);
    }

    @Override
    protected int getFileUidOrThrow(@NonNull FileDescriptor fileDescriptor) throws FileNotFoundException {
        try {
            StructStat st = Os.fstat(fileDescriptor);
            return st.st_uid;
        } catch (android.system.ErrnoException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
