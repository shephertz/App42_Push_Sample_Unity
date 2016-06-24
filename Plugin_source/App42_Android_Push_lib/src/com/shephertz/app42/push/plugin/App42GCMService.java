/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.push.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;


/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 * @author Vishnu Garg
 */

public class App42GCMService extends IntentService {
	public static final int NotificationId = 1;
	public static final String ExtraMessage = "message";
	static int msgCount = 0;

	public App42GCMService() {
		super("App42GCMService");
	}

	public static final String TAG = "App42 Push";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				///sendNotification("Send error: " + extras.toString());
				App42UnityHelper.sendGCMError(extras.toString());
				Log.e("Send error:", extras.toString());
			
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				Log.e("Deleted messages on GCM server:", extras.toString());
				App42UnityHelper.sendGCMError(extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// This loop represents the service doing some work.
				
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				// Post notification of received message.
				Bundle bundle = intent.getExtras();
				String message = (String) bundle.get("message");
				if(message==null)
					return;
				boolean isShow=false;
				try {
					
					App42UnityHelper.saveLastMessage(message, this);
					App42UnityHelper.sendPushMessage(message,this);
					//if(!isRunning()){
						Log.i(TAG, "Application is Not running closed");
					sendNotification(message);
					isShow=true;
//					}
//					else{
//						Log.i(TAG, "Application is  running closed");
//					}
				} catch (Throwable e) {
				
					Log.i(TAG, "Application is currently closed"+e.getMessage());
					if(!isShow)
						try {
							sendNotification(message);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
				}
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		try{
		App42GCMReceiver.completeWakefulIntent(intent);
		}catch (Throwable e) 
		{}
	}
	public boolean isRunning() {

	    ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> services = activityManager
	            .getRunningTasks(Integer.MAX_VALUE);
	    boolean isActivityFound = false;

	    if (services.get(0).topActivity.getPackageName().toString()
	            .equalsIgnoreCase(getPackageName().toString())) {
	        isActivityFound = true;
	    }
	 return isActivityFound;
	}
	private JSONObject getMessageJson(String message){
		JSONObject json=null;
		try {
			 json=new JSONObject(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				json=new JSONObject();
				json.put("message", message);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
			
			}
		}
		return json;
	}
	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	/**
	 * @param msg
	 * @throws JSONException 
	 */
	private void sendNotification(String message) throws JSONException {
		JSONObject messageJsonObject=getMessageJson(message);
		if(messageJsonObject==null)
			return;
		long when = System.currentTimeMillis();;
		int iconId=getResources().getIdentifier("app_icon", "drawable", getPackageName());
		Intent notificationIntent;
		try {
			notificationIntent = new Intent(this,
					Class.forName(getActivityName()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			notificationIntent = new Intent();
		}
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new NotificationCompat.Builder(this)
				.setContentTitle(getTitle()).setContentText(messageJsonObject.getString("message"))
				.setContentIntent(intent)
				.setSmallIcon(iconId).setWhen(when)
				.setNumber(++msgCount)
				//.setLargeIcon(getBitmapFromAssets())
				.setLights(Color.YELLOW, 1, 2).setAutoCancel(true).build();

		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}
	
	/**
	 * @return
	 */
	private String getActivityName() {
		ApplicationInfo ai;
		try {
			ai = this.getPackageManager().getApplicationInfo(
					this.getPackageName(), PackageManager.GET_META_DATA);
			Bundle aBundle = ai.metaData;
			return aBundle.getString("onMessageOpen");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return Title From Android Manifest file
	 */
	private String getTitle() {
		try {
			ApplicationInfo ai = getPackageManager()
					.getApplicationInfo(this.getPackageName(),
							PackageManager.GET_META_DATA);
			Bundle aBundle = ai.metaData;
			return aBundle.getString("push_title");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "App42PushSample";
		}
	}
	public Bitmap getBitmapFromAssets() {
		AssetManager assetManager = getAssets();
		InputStream istr;
		try {
			istr = assetManager.open("app_icon.png");
			Bitmap bitmap = BitmapFactory.decodeStream(istr);
			return bitmap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**
     * This message reset message count shown in Notification Bar
     */
	public static void resetMsgCount() {
		msgCount = 0;
	}
}
