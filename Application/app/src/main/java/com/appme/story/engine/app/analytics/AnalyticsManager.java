package com.appme.story.engine.app.analytics;

import android.support.annotation.NonNull;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Process;
import android.os.Environment;
import android.net.Uri;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.appme.story.R;
import com.appme.story.BuildConfig;
import com.appme.story.engine.app.analytics.RandomEvent;
import com.appme.story.engine.app.folders.FolderMe;

public class AnalyticsManager
{
	private static Context mContext;
   	private static File log;
	private static File initFolder;

    private final static String TAG = AnalyticsManager.class.getSimpleName();

	public AnalyticsManager(Context mContext)
	{
		this.mContext = mContext;
	}

    public AnalyticsManager intialize(Context context)
	{

	    initFolder = new File(FolderMe.getAppMeFolder());
		if (initFolder.exists())
		{
			initFolder.mkdirs();
		}

		return this;
    }


    public AnalyticsManager setProperty()
	{
		try
		{
			File log = new File(FolderMe.getAppMeFolder(), "README.md");
			log.getParentFile().mkdirs();
			FileUtils.writeStringToFile(log, mContext.getResources().getString(R.string.app_author));
		}
		catch (IOException e)
		{
			Log.d(TAG, e.getMessage());
		}


		return this;
    }

	public AnalyticsManager setProperty(String folder, String propertyName, String propertyValue)
	{

		File log = new File(folder, propertyName);
		log.getParentFile().mkdirs();
		File nomedia = new File(log.getParent(), ".nomedia");
		if (!nomedia.exists() || !nomedia.isFile())
		{
			try
			{
				nomedia.createNewFile();
			}
			catch (IOException e)
			{
				Log.d(TAG, e.getMessage());
			}
		}
		writeStringToFile(log, propertyValue);
		return this;
    }

	public AnalyticsManager setCurrentScreen(String screenName)
	{

		String externalFileDir = mContext.getExternalFilesDir("analytics").getAbsolutePath();
		log = new File(externalFileDir, "antidoze-log.txt");
		log.getParentFile().mkdirs();

		RandomEvent event = new RandomEvent(screenName);
		writeStringToFile(log, event);
		return this;
    }

	public static void setLogEvent(String eventLog)
	{
		String externalFileDir = mContext.getExternalFilesDir("analytics").getAbsolutePath();
		File log = new File(externalFileDir, "event-log.txt");
		log.getParentFile().mkdirs();
		RandomEvent event = new RandomEvent(eventLog);
		writeStringToFile(log, event);
    }

	public static void setBrowserUrl(String url)
	{
		try
		{
            JSONObject json = new JSONObject();
            json.put("url", url);
            String filePath = FolderMe.FOLDER_YOUTUBE + "/event-browserUrl-log.json";
			File log = new File(filePath);
			log.getParentFile().mkdirs();
            FileUtils.writeStringToFile(log, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }

	public static void setBrowserDownload(String url, String suggestedFileName, String mimeType, long contentLength, String contentDisposition, String userAgent)
	{
		try
		{
            JSONObject json = new JSONObject();
            json.put("url", url);
            json.put("suggestedFileName", suggestedFileName);
			json.put("mimeType", mimeType);
			json.put("contentLength", contentLength);
			json.put("contentDisposition", contentDisposition);
			json.put("userAgent", userAgent);
			File log = new File(FolderMe.FOLDER_YOUTUBE, "event-browser-log.json");
			log.getParentFile().mkdirs();
            FileUtils.writeStringToFile(log, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }

	public static void setBrowserHandleDownload(String url, String suggestedFileName)
	{
		try
		{
            JSONObject json = new JSONObject();
            json.put("url", url);
            json.put("suggestedFileName", suggestedFileName);
			File log = new File(FolderMe.FOLDER_YOUTUBE, "event-browserDownload-log.json");
			log.getParentFile().mkdirs();
            FileUtils.writeStringToFile(log, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }

	public static void YoutubeLog(String videoUrl)
	{
		try
		{
            JSONObject json = new JSONObject();
            json.put("videoUrl", videoUrl);
            String filePath = FolderMe.FOLDER_YOUTUBE + "/event-videoUrl-log.json";
			File log = new File(filePath);
			log.getParentFile().mkdirs();
            FileUtils.writeStringToFile(log, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }

	public static void YoutubeLog(String title, String videoId, String message)
	{
		try
		{
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("videoUrl", videoId);
			json.put("message", message);
			String filePath = FolderMe.FOLDER_YOUTUBE + "/event-videoPlaying-log.json";
			File log = new File(filePath);
			log.getParentFile().mkdirs();
            FileUtils.writeStringToFile(log, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }

    public static void YoutubeLog(String title, String thumbnail, String videoId, String published)
	{
		try
		{
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("thumbnails", thumbnail);
            json.put("videoId", videoId);
			json.put("publishedAt", published);
            String filePath = FolderMe.FOLDER_YOUTUBE + "/event-playlist-log.json";
			File log = new File(filePath);
			log.getParentFile().mkdirs();
            FileUtils.writeStringToFile(log, json.toString());
        }
		catch (IOException | JSONException e)
		{
            e.printStackTrace();
        }
    }

	public static String getBrowserUrl()
	{
		try
		{
		    File infoFile = new File(FolderMe.FOLDER_YOUTUBE + "/event-browser-log.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("url");
		}
		catch (IOException | JSONException e)
		{
            return null;
		}
	}

	public static String getBrowserUrlDownload()
	{
		try
		{
		    File infoFile = new File(FolderMe.FOLDER_YOUTUBE + "/event-browserDownload-log.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("url");
		}
		catch (IOException | JSONException e)
		{
            return null;
		}
	}

	public static String getBrowserFileDownload()
	{
		try
		{
		    File infoFile = new File(FolderMe.FOLDER_YOUTUBE + "/event-browserDownload-log.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("suggestedFileName");
		}
		catch (IOException | JSONException e)
		{
            return null;
		}
	}

	private static void writeStringToFile(File f, String event)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(f, true);
			Writer osw = new OutputStreamWriter(fos);
			osw.write(event);
			osw.write('\n');
			osw.flush();
			fos.flush();
			fos.getFD().sync();
			fos.close();

			Log.d(TAG, event);
		}
		catch (IOException e)
		{
			Log.e(TAG, "Exception writing to file", e);

		}
	}


	public static void writeStringToFile(File f, JSONObject event)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(f, true);
			Writer osw = new OutputStreamWriter(fos);
			osw.write(event.toString() + ",");
			osw.write('\n');
			osw.flush();
			fos.flush();
			fos.getFD().sync();
			fos.close();

			Log.d(TAG, event.toString());
		}
		catch (IOException e)
		{
			Log.e(TAG, "Exception writing to file", e);
		}
	}

	public static void writeStringToFile(File f, RandomEvent event)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(f, true);
			Writer osw = new OutputStreamWriter(fos);

			osw.write(event.when.toString());
			osw.write(" : ");
			osw.write(event.value);
			osw.write('\n');
			osw.flush();
			fos.flush();
			fos.getFD().sync();
			fos.close();

			Log.d(TAG, "logged to " + f.getAbsolutePath());
		}
		catch (IOException e)
		{
			Log.e(TAG, "Exception writing to file", e);
		}
	}

	public static boolean isConnect()
	{
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
		{
            return true;
        }
        return false;
    }

	public static final boolean isInternetOn(Context context)
	{

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Check for network connections
		if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
			connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
			connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
			connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED)
		{

			// if connected with internet

			Toast.makeText(context, " Connected ", Toast.LENGTH_LONG).show();
			return true;

		}
		else if ( 
			connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
			connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED)
		{
			Toast.makeText(context, " Not Connected ", Toast.LENGTH_LONG).show();
			return false;
		}
		return false;
	}

	public static void checkUrl(String url)
	{
        if (!url.contains("youtube.com/watch"))
		{

			try
			{
				Uri uri = Uri.parse(url);
				String videoId = uri.getQueryParameter("v");
				if (videoId == null)
				{
					YoutubeLog(videoId);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

        }
		else
		{
			try
			{
				Uri uri = Uri.parse(url);
				String videoId = uri.toString();
				if (videoId == null)
				{
					YoutubeLog(videoId);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
    }

	public static boolean isIntalled(Context context, String packageName)
	{
        boolean exist = false;
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList)
		{
            if (resolveInfo.activityInfo.packageName.equals(packageName))
			{
                exist = true;
            }
        }
        return exist;
    }
}

	
