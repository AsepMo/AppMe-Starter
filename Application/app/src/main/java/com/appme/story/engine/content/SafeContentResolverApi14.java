package com.appme.story.engine.content;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;

import android.content.Context;
import android.support.annotation.NonNull;


class SafeContentResolverApi14 extends SafeContentResolver {
    SafeContentResolverApi14(Context context) {
        super(context);
        Os.init(context);
    }

    @Override
    protected int getFileUidOrThrow(@NonNull FileDescriptor fileDescriptor) throws FileNotFoundException {
        try {
            int systemFileDescriptor = extractSystemFileDescriptor(fileDescriptor);

            return Os.fstat(systemFileDescriptor);
        } catch (ErrnoException | UnsupportedOperationException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    private int extractSystemFileDescriptor(FileDescriptor fileDescriptor) throws FileNotFoundException {
        Field descriptor;
        try {
            descriptor = fileDescriptor.getClass().getDeclaredField("descriptor");
        } catch (NoSuchFieldException e) {
            throw new FileNotFoundException("Couldn't find field that holds system file descriptor");
        }

        descriptor.setAccessible(true);

        try {
            return descriptor.getInt(fileDescriptor);
        } catch (IllegalAccessException e) {
            throw new FileNotFoundException("Couldn't read system file descriptor");
        }
    }
}
