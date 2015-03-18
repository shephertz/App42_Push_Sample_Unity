/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.push.plugin;

import java.io.IOException;
import java.io.InputStream;

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
				for (int i = 0; i < 5; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				// Post notification of received message.
				Bundle bundle = intent.getExtras();
				String message = (String) bundle.get("message");
				sendNotification(message);
				try {
					App42UnityHelper.sendPushMessage(message);
				} catch (Throwable e) {
					Log.e("App42 Exception", e.getMessage());
				}
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		App42GCMReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	/**
	 * @param msg
	 */
	private void sendNotification(String message) {
		
		long when = System.currentTimeMillis();;
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
				.setContentTitle(getTitle()).setContentText(message)
				.setContentIntent(intent)
				.setSmallIcon(android.R.drawable.ic_dialog_info).setWhen(when)
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
