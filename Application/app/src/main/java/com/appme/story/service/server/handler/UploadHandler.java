package com.appme.story.service.server.handler;

import com.appme.story.AppController;

import android.os.Environment;

import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.upload.HttpFileUpload;
import com.yanzhenjie.andserver.upload.HttpUploadContext;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.httpcore.HttpEntityEnclosingRequest;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>Upload file handler.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class UploadHandler implements RequestHandler {

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (!HttpRequestParser.isMultipartContentRequest(request)) { // Is POST and upload.
            response(403, "You must upload file.", response);
        } else {
            final File saveDirectory = Environment.getExternalStorageDirectory();

            if (saveDirectory.isDirectory()) {
                try {
                    processFileUpload(request, saveDirectory);
                    response(200, "Ok.", response);
                } catch (Exception e) {
                    response(500, "Save the file when the error occurs.", response);
                }
            } else {
                response(500, "The server can not save the file.", response);
            }
        }
    }

    private void response(int responseCode, String message, HttpResponse response) throws IOException {
        response.setStatusCode(responseCode);
        response.setEntity(new StringEntity(message, "utf-8"));
    }

    /**
     * Parse file and save.
     *
     * @param request       request.
     * @param saveDirectory save directory.
     * @throws Exception may be.
     */
    private void processFileUpload(HttpRequest request, File saveDirectory) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory(1024 * 1024, saveDirectory);
        HttpFileUpload fileUpload = new HttpFileUpload(factory);

        // Set upload process listener.
        // fileUpload.setProgressListener(new ProgressListener(){...});

        List<FileItem> fileItems = fileUpload.parseRequest(new HttpUploadContext((HttpEntityEnclosingRequest) request));

        for (FileItem fileItem : fileItems) {
            if (!fileItem.isFormField()) { // File param.
                // Attribute.
                // fileItem.getContentType();
                // fileItem.getFieldName();
                // fileItem.getName();
                // fileItem.getSize();
                // fileItem.getString();

                File uploadedFile = new File(saveDirectory, fileItem.getName());
                // ????????????????????????
                fileItem.write(uploadedFile);
            } else { // General param.
                String key = fileItem.getName();
                String value = fileItem.getString();
            }
        }
    }
}
