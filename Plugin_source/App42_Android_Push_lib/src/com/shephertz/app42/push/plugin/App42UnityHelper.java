/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.push.plugin;

import android.content.SharedPreferences;
import android.util.Log;

import com.shephertz.app42.push.plugin.App42GCMController.App42GCMListener;
import com.unity3d.player.UnityPlayer;
/**
 * @author Vishnu Garg
 */
public class App42UnityHelper {

	private final static String KeyLastMessage = "lastMessage";
	private final static String AppName = "PushUnityNotification";
	private final static String KeyGameObject = "gameObjects";
	private final static String OnMessage = "onPushNotificationsReceived";
	private final static String OnRegistrationId = "onDidRegisterForRemoteNotificationsFromNative";
	private final static String OnGCMRegistrationError = "onDidFailToRegisterForRemoteNotificationFromNative";
	private final static String OnGCMError = "onErrorFromNative";
    
	/*
	 * This function allows to register device for PushNotification service
	 */
	public static void registerOnGCM(String projectNo) {
		try {
			if (App42GCMController
					.isPlayServiceAvailable(UnityPlayer.currentActivity)) {
				App42GCMController.getRegistrationId(
						UnityPlayer.currentActivity, projectNo,
						new App42GCMListener() {
							@Override
							public void onGCMRegistrationId(String gcmRegId) {
								App42GCMController.storeRegistrationId(
										UnityPlayer.currentActivity, gcmRegId);
								sendGCMRegId(gcmRegId);
							}

							@Override
							public void onError(String errorMsg) {
								
								sendGCMRegistrationError(errorMsg);
							}

						});
			} else {
				Log.i("App42PushNotification",
						"No valid Google Play Services APK found.");
				sendGCMRegistrationError("No valid Google Play Services APK found.");
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/** Sends Push Message to Unity
	 * @param message
	 */
	public static void sendPushMessage(String message) {
		saveLastMessage(message);
		UnityPlayer.UnitySendMessage(getGameObjectName(), OnMessage, message);
		Log.i("App42 Message Sent : OnMessage", message);
	}

	/** Sends RegistrationId to Unity 
	 * @param regId
	 */
	public static void sendGCMRegId(String regId) {
		UnityPlayer.UnitySendMessage(getGameObjectName(), OnRegistrationId, regId);
		Log.i(" App42 RegId Sent : OnRegistrationId", regId);
	}

	/**This function send GCM Error to Unity
	 * @param error
	 */
	public static void sendGCMRegistrationError(String error) {
		UnityPlayer.UnitySendMessage(getGameObjectName(), OnGCMRegistrationError, error);
		Log.i(" GCM Registration Error :", error);
	}
	/**This function send GCM Error to Unity
	 * @param error
	 */
	public static void sendGCMError(String error) {
		UnityPlayer.UnitySendMessage(getGameObjectName(), OnGCMError, error);
		Log.i(" GCM Registration Error :", error);
	}
	

	/** This function send last Push message to Unity
	 * @return
	 */
	public static String getLastMessage() {
		resetCount();
		SharedPreferences sharedPreference = UnityPlayer.currentActivity
				.getSharedPreferences(AppName,
						UnityPlayer.currentActivity.MODE_PRIVATE);
		return sharedPreference.getString(KeyLastMessage, "");
	}

	/** This function save last Push Notification message in prferences
	 * @param projectNo
	 */
	private static void saveLastMessage(String message) {
		SharedPreferences sharedPreference = UnityPlayer.currentActivity
				.getSharedPreferences(AppName,
						UnityPlayer.currentActivity.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = sharedPreference.edit();
		prefEditor.putString(KeyLastMessage, message);
		prefEditor.commit();
	}

	/** 
	 * Save gameObject Name on which we have to send regId and Push messagess
	 * @param gameObjectName
	 */
	private static void setListener(String gameObjectName){
		SharedPreferences sharedPreference = UnityPlayer.currentActivity
				.getSharedPreferences(AppName,
						UnityPlayer.currentActivity.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = sharedPreference.edit();
		prefEditor.putString(KeyGameObject, gameObjectName);
		prefEditor.commit();
	}
	
	/** GameObject name on which Push message and regId has to be sent
	 * @return
	 */
	private static String getGameObjectName(){
		SharedPreferences sharedPreference = UnityPlayer.currentActivity
				.getSharedPreferences(AppName,
						UnityPlayer.currentActivity.MODE_PRIVATE);
		return sharedPreference.getString(KeyGameObject, "");
	}
	/*
	 * Call This Function from Unity after Message is shown on Unity Screen This
	 * function reset PushMessage Count to zero
	 */
	public static void resetCount() {
		App42GCMService.msgCount = 0;
	}

}
