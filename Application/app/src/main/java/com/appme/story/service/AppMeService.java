package com.appme.story.service;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.app.Service;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.net.Uri;
import android.media.ToneGenerator;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.filter.HttpCacheFilter;
import com.yanzhenjie.andserver.website.AssetsWebsite;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.app.utils.NetWorkUtils;
import com.appme.story.receiver.SendBroadcast;
import com.appme.story.service.server.ServerManager;
import com.appme.story.service.server.TinyWebServer;
import com.appme.story.service.server.handler.FileHandler;
import com.appme.story.service.server.handler.ImageHandler;
import com.appme.story.service.server.handler.LoginHandler;
import com.appme.story.service.server.handler.UploadHandler;

public class AppMeService extends Service {

    public static String TAG = AppMeService.class.getSimpleName();  
    private static AppMeService foregroundService;

    private static final boolean DEBUG = false;

    private WindowManager mWindowManager;
    /**
     * AppMe Server.
     */
    private Server mServer;
    private final HandlerThread handlerThread = new HandlerThread(getClass().getSimpleName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
    private Handler mHandler;

    private Runnable mRunner = new Runnable(){
        @Override
        public void run() {

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
    };

    private static boolean isRunning;
    private final ToneGenerator beeper = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
    private boolean isForeground = false;
    // Fields for notifications
    private Notification startNotification;
    private final Intent startStreamIntent = new Intent(SendBroadcast.START_SERVICE);
    private final Intent stopStreamIntent = new Intent(SendBroadcast.STOP_SERVICE);
    private final Intent closeIntent = new Intent(SendBroadcast.SERVICE_IS_SHUTDOWN);
    private BroadcastReceiver broadcastReceiver;
    
    private RemoteViews mContentViewBig, mContentViewSmall;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("Binding not supported. Go away.");
    }

    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    public Handler getHandler() {
        return mHandler;
    }

    @Override 
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.v(TAG, "onCreate:");
        foregroundService = this;
        
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        isRunning = true;
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        mHandler.postDelayed(mRunner, 1200);
       
        // Registering receiver for screen off messages
        final IntentFilter screenOnOffFilter = new IntentFilter();
        screenOnOffFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenOnOffFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_SCREEN_OFF)) {

                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                }              
            }
        };

        registerReceiver(broadcastReceiver, screenOnOffFilter);
    }

    /**
     * Server listener.
     */
    private Server.ServerListener mListener = new Server.ServerListener() {
        @Override
        public void onStarted() {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            ServerManager.serverStart(AppMeService.this, hostAddress);
        }

        @Override
        public void onStopped() {
            ServerManager.serverStop(AppMeService.this);
        }

        @Override
        public void onError(Exception e) {
            ServerManager.serverError(AppMeService.this, e.getMessage());
        }
    };
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.v(TAG, "onStartCommand:intent=" + intent);

        if (intent.getAction() == null) {
            String message = intent.getStringExtra(SendBroadcast.EXTRA_SERVICE);

            showNotification(); 
            beeper.startTone(ToneGenerator.TONE_PROP_ACK);
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.SERVICE_IS_READY, message);           
            isRunning = true;
            
        } else if (SendBroadcast.ACTION.START_SERVICE.equals(intent.getAction())) {
            startServer();
            showNotification();
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.START_SERVICE, "Service Is Starter");           

        } else if (SendBroadcast.ACTION.PAUSE_SERVICE.equals(intent.getAction())) {       
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.PAUSE_SERVICE, "Service Is Paused");           

        } else if (SendBroadcast.ACTION.START_ACTIVITY.equals(intent.getAction())) {
            //SendBroadcast.getInstance().broadcastStatus(SendBroadcast.START_ACTIVITY, "Start Activity");           
            ApplicationActivity.start(this);
        } else if (SendBroadcast.ACTION.RESUME_SERVICE.equals(intent.getAction())) {
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.RESUME_SERVICE, "Service Is Resumed");           

        } else if (SendBroadcast.ACTION.OPEN_BROWSER.equals(intent.getAction())) {
            String mRootUrl = AppController.getServerIP();
            if (!TextUtils.isEmpty(mRootUrl)) {
                SendBroadcast.getInstance().broadcastStatus(SendBroadcast.OPEN_BROWSER, "Open Browser");                         
            } 
            ApplicationActivity.startChrome(this, mRootUrl);
            
        } else if (SendBroadcast.ACTION.SHUTDOWN_SERVICE.equals(intent.getAction())) {
            beeper.startTone(ToneGenerator.TONE_PROP_NACK);
            if (!isRunning) {
                foregroundify();           
            }
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.SERVICE_IS_SHUTDOWN, "Service Is Shutdown");                                 
            stopForeground(true);
            stopSelf();
        }

        return(START_NOT_STICKY);

    }

    public static boolean isRunning() {
        return isRunning;
    }

    public boolean isConnected(){
        return NetWorkUtils.isConnected(this);
    }
    /**
     * Start server.
     */
    private void startServer() {
        if (mServer != null) {
            if (mServer.isRunning()) {
                String hostAddress = mServer.getInetAddress().getHostAddress();
                ServerManager.serverStart(AppMeService.this, hostAddress);
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
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ApplicationActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setSmallIcon(R.drawable.apk_v2);  // the status icon
        notification.setWhen(System.currentTimeMillis());  // the time stamp
        notification.setContentIntent(contentIntent);  // The intent to send when the entry is clicked
        notification.setCustomContentView(getSmallContentView());
        notification.setCustomBigContentView(getBigContentView());
        notification.setPriority(NotificationCompat.PRIORITY_MAX);
        notification.setOngoing(true);

        Notification notif = notification.build();
        // Send the notification.
        if (isForeground) {
            mgr.notify(SendBroadcast.NOTIFY_ID, notif);
        } else {
            startForeground(SendBroadcast.NOTIFY_ID, notif);
            isForeground = true;
        }
        startForeground(SendBroadcast.NOTIFY_ID, notif);
    }

    private RemoteViews getSmallContentView() {
        if (mContentViewSmall == null) {
            mContentViewSmall = new RemoteViews(getPackageName(), R.layout.remote_view_appme_small);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

    private RemoteViews getBigContentView() {
        if (mContentViewBig == null) {
            mContentViewBig = new RemoteViews(getPackageName(), R.layout.remote_view_appme);
            setUpRemoteView(mContentViewBig);
        }
        updateRemoteViews(mContentViewBig);
        return mContentViewBig;
    }

    private void setUpRemoteView(RemoteViews remoteView) {
        remoteView.setImageViewResource(R.id.image_view_close, R.drawable.ic_app_close);
        remoteView.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_app_home);
        remoteView.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_google_chrome);

        remoteView.setOnClickPendingIntent(R.id.button_close, buildPendingIntent(SendBroadcast.ACTION.SHUTDOWN_SERVICE));
        remoteView.setOnClickPendingIntent(R.id.button_play_last, buildPendingIntent(SendBroadcast.ACTION.START_ACTIVITY));
        remoteView.setOnClickPendingIntent(R.id.button_play_next, buildPendingIntent(SendBroadcast.ACTION.OPEN_BROWSER));
        remoteView.setOnClickPendingIntent(R.id.button_play_toggle, buildPendingIntent(SendBroadcast.ACTION.PLAY_TOGGLE));
    }

    private void updateRemoteViews(RemoteViews remoteView) {
        String serverIP = AppController.getServerIP();
        if (serverIP != null) {
            remoteView.setTextViewText(R.id.text_view_name, getString(R.string.appme_service));
            if(mServer != null && mServer.isRunning()){
                remoteView.setTextViewText(R.id.text_view_artist, getString(R.string.appme_service_is_running));
            }else{
                remoteView.setTextViewText(R.id.text_view_artist, getString(R.string.appme_service_not_running));             
            }
        }
        remoteView.setImageViewResource(R.id.image_view_play_toggle, R.drawable.ic_monitor_screenshot);
        remoteView.setImageViewResource(R.id.image_view_album, R.drawable.apk_v2);
    }

    private void foregroundify() {
        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mgr.getNotificationChannel(SendBroadcast.CHANNEL_WHATEVER) == null) {
            mgr.createNotificationChannel(new NotificationChannel(SendBroadcast.CHANNEL_WHATEVER, "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
                                                            
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, SendBroadcast.CHANNEL_WHATEVER);
        b.setTicker(getString(R.string.app_name));    
        b.setWhen(System.currentTimeMillis());
        b.setAutoCancel(true);
        b.setDefaults(Notification.DEFAULT_ALL);
        b.setContentTitle(getString(R.string.appme_service));
        b.setContentText(getString(R.string.appme_service_is_running));
        b.setContentIntent(buildPendingIntent(SendBroadcast.START_ACTIVITY));
        b.setSmallIcon(R.mipmap.ic_launcher);
        b.addAction(R.drawable.ic_app_home, getString(R.string.appme_service_is_home), buildPendingIntent(SendBroadcast.ACTION.START_ACTIVITY));
        b.addAction(R.drawable.ic_google_chrome, getString(R.string.appme_start_browser), buildPendingIntent(SendBroadcast.ACTION.OPEN_BROWSER));

        if (isForeground) {
            mgr.notify(SendBroadcast.NOTIFY_ID, b.build());
        } else {
            startForeground(SendBroadcast.NOTIFY_ID, b.build());
            isForeground = true;
        }
        startForeground(SendBroadcast.NOTIFY_ID, b.build());
    }

    

    // Private methods
    /*private Notification getNotificationStart() {
        final Intent mainActivityIntent = new Intent(this, ApplicationActivity.class);
        //mainActivityIntent.setAction(ApplicationActivity.ACTION_SCREEN_SERVER);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent pendingMainActivityIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        final NotificationCompat.Builder startNotificationBuilder = new NotificationCompat.Builder(this);
        startNotificationBuilder.setSmallIcon(R.drawable.apk_v2);
        startNotificationBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        startNotificationBuilder.setContentTitle(getResources().getString(R.string.service_ready_to_stream));
        startNotificationBuilder.setContentText(getResources().getString(R.string.service_press_start));
        startNotificationBuilder.setContentIntent(pendingMainActivityIntent);
        startNotificationBuilder.addAction(R.drawable.ic_app_home, getResources().getString(R.string.service_start), PendingIntent.getBroadcast(this, 0, startStreamIntent, 0));
        startNotificationBuilder.addAction(R.drawable.ic_app_close, getResources().getString(R.string.service_close), PendingIntent.getBroadcast(this, 0, closeIntent, 0));
        startNotificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        return startNotificationBuilder.build();
    }

    private Notification getNotificationStop() {
        final Intent mainActivityIntent = new Intent(this, ApplicationActivity.class);
        //mainActivityIntent.setAction(MonitorActivity.ACTION_SCREEN_SERVER);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent pendingMainActivityIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        final NotificationCompat.Builder stopNotificationBuilder = new NotificationCompat.Builder(this);
        stopNotificationBuilder.setSmallIcon(R.drawable.apk_v2);
        stopNotificationBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        stopNotificationBuilder.setContentTitle(getResources().getString(R.string.service_stream));
        stopNotificationBuilder.setContentText(getResources().getString(R.string.service_go_to) + AppController.getServerIP());
        stopNotificationBuilder.setContentIntent(pendingMainActivityIntent);
        stopNotificationBuilder.addAction(R.drawable.ic_stop, getResources().getString(R.string.service_stop), PendingIntent.getBroadcast(this, 0, stopStreamIntent, 0));
        stopNotificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        return stopNotificationBuilder.build();
    }*/

    // PendingIntent
    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getService(this, 0, new Intent(action), 0);
    }

    private PendingIntent buildPendingIntent(String action) {
        Intent i = new Intent(this, getClass());
        i.setAction(action);
        return(PendingIntent.getService(this, 0, i, 0));
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
       //foregroundServiceTaskHandler.getLooper().quit();
        stopForeground(true);  
        stopServer(); // Stop server.
        //stop webserver on destroy of service or process
        TinyWebServer.stopServer();
    } 
}
