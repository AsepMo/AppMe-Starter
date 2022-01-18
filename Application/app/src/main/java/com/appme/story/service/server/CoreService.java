package com.appme.story.service.server;

import com.appme.story.R;
import com.appme.story.service.server.handler.FileHandler;
import com.appme.story.service.server.handler.ImageHandler;
import com.appme.story.service.server.handler.LoginHandler;
import com.appme.story.service.server.handler.UploadHandler;
import com.appme.story.engine.app.utils.NetWorkUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.filter.HttpCacheFilter;
import com.yanzhenjie.andserver.website.AssetsWebsite;

import java.util.concurrent.TimeUnit;

public class CoreService extends Service {

    /**
     * AndServer.
     */
    private Server mServer;

    @Override
    public void onCreate() {
        // More usage documentation: http://yanzhenjie.github.io/AndServer
        mServer = AndServer.serverBuilder()
                .inetAddress(NetWorkUtils.getLocalIPAddress()) // Bind IP address.
                .port(8080)
                .timeout(10, TimeUnit.SECONDS)
                .website(new AssetsWebsite(getAssets(), "web"))
                .registerHandler("/download", new FileHandler())
                .registerHandler("/login", new LoginHandler())
                .registerHandler("/upload", new UploadHandler())
                .registerHandler("/image", new ImageHandler())
                .filter(new HttpCacheFilter())
                .listener(mListener)
                .build();
        //call contructor with local ip, port , public html directory path
        TinyWebServer.startServer(NetWorkUtils.getLocalIpAddress(), 9000, "/web/public_html");      
    }

    /**
     * Server listener.
     */
    private Server.ServerListener mListener = new Server.ServerListener() {
        @Override
        public void onStarted() {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            ServerManager.serverStart(CoreService.this, hostAddress);
        }

        @Override
        public void onStopped() {
            ServerManager.serverStop(CoreService.this);
        }

        @Override
        public void onError(Exception e) {
            ServerManager.serverError(CoreService.this, e.getMessage());
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer(); // Stop server.
        //stop webserver on destroy of service or process
        TinyWebServer.stopServer();
    }

    /**
     * Start server.
     */
    private void startServer() {
        if (mServer != null) {
            if (mServer.isRunning()) {
                String hostAddress = mServer.getInetAddress().getHostAddress();
                ServerManager.serverStart(CoreService.this, hostAddress);
            } else {
                mServer.startup();
            }
        }
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        if (mServer != null && mServer.isRunning()) {
            mServer.shutdown();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
