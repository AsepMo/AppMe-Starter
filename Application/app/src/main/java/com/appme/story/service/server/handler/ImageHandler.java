package com.appme.story.service.server.handler;

import com.appme.story.R;
import com.appme.story.AppController;
import com.yanzhenjie.andserver.SimpleRequestHandler;
import com.yanzhenjie.andserver.view.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.FileEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static com.yanzhenjie.andserver.util.FileUtils.getMimeType;

/**
 * Created by YanZhenjie on 2017/12/20.
 */
public class ImageHandler extends SimpleRequestHandler {

    private File mFile = new File(Environment.getExternalStorageDirectory(), "xxx.jpg");

    @Override
    protected View handle(HttpRequest request) throws HttpException, IOException {
        writeToSdCard();

        HttpEntity httpEntity = new FileEntity(mFile, ContentType.create(getMimeType(mFile.getAbsolutePath()), Charset.defaultCharset()));
        return new View(200, httpEntity);
    }

    private void writeToSdCard() throws IOException {
        if (!mFile.exists()) {
            synchronized (ImageHandler.class) {
                if (!mFile.exists()) {
                    boolean b = mFile.createNewFile();
                    if (!b) {
                        throw new IOException("What broken cell phone.");
                    }

                    Bitmap bitmap = BitmapFactory.decodeResource(AppController.getContext().getResources(), R.drawable.aweb_icon);
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(mFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (outputStream != null) {
                            outputStream.flush();
                            outputStream.close();
                        }
                    }
                }
            }
        }
    }

}
