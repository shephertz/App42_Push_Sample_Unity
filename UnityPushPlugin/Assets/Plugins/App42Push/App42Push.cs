/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */

using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System;
using System.Text;
using AssemblyCSharpfirstpass;
/**
 * @author Vishnu Garg
 */
public class App42Push : MonoBehaviour
{
		private static App42Push mInstance = null;
		private static App42NativePushListener app42Listener;

        /**
        * Registration for Push Notification on GCM and APNs
        */
		public static void registerForPush (string projectNumber)
		{
				#if UNITY_ANDROID
		registerOnGCM(projectNumber);
				#endif
			
		}
        /**
        * @return last save Pushmessage
        */
		public static string getLastPushMessage ()
		{
				string message = "";
				#if UNITY_ANDROID
		message=getLastAndroidMessage();
				#endif
				return message;
		}
       /**
        * Set CallBack GameObject on which get callback from native
        */
	private static void setNativeCallbackListener ()
		{
				if (mInstance == null) {
						GameObject receiverObject = new GameObject ("App42Push");
						DontDestroyOnLoad (receiverObject);
						mInstance = receiverObject.AddComponent<App42Push> ();
						setListenerGameObject(receiverObject.name);
				}
		}
        /**
        * @return deviceType
        */
		public static string getDeviceType ()
		{
			string deviceType = null;
				#if UNITY_ANDROID
		        deviceType= "Android";
				#endif
		   return deviceType;
		}
        /**
        * Set callback listener
        */
		public static void setApp42PushListener (App42NativePushListener listener)
		{
				app42Listener = listener;
				setNativeCallbackListener ();
		}
	    /**
        * Callback from native when Push Notification is being received
        */
		public void onPushNotificationsReceived (String message)
		{ 
				Debug.Log (message);
				if (app42Listener != null && message != null) {
						app42Listener.onMessage (message);

				}
		}
        /**
        * Callback from native when any error is being received
        */
		public void onErrorFromNative (string error)
		{
				Debug.Log (error);
				if (app42Listener != null && error != null) {
						app42Listener.onError (error);
		
				}
		}
        /**
        * Callback from native when registration failed
        */
		public void onDidFailToRegisterForRemoteNotificationFromNative (string error)
		{
			Debug.Log (error);
			if (app42Listener != null && error != null) {
				app42Listener.onError (error);
				
			}
		}
	   /**
        * Callback from native when registration Id received
        */
		public void onDidRegisterForRemoteNotificationsFromNative (String deviceToken)
		{ 
				Debug.Log (deviceToken);
				if (app42Listener != null) {
						if (deviceToken != null && deviceToken.Length != 0) {
								app42Listener.onDeviceToken (deviceToken);
						}
				}
		}
       /**
        * Set GameObject listener from Native
        */
		public static void setListenerGameObject(String gameObjectName)
		{
		#if UNITY_ANDROID
       setListener(gameObjectName);
		#endif
		}

	#if UNITY_ANDROID
		public static void registerOnGCM(string projectNo) {
      if(Application.platform != RuntimePlatform.Android) return;
      AndroidJNIHelper.debug = false; 
		using (AndroidJavaClass jc = new AndroidJavaClass("com.shephertz.app42.push.plugin.App42UnityHelper")) { 
			jc.CallStatic("registerOnGCM", projectNo);
          }
        }
		public static string getLastAndroidMessage() {
				if (Application.platform != RuntimePlatform.Android)
						return null;
				AndroidJNIHelper.debug = false; 
				using (AndroidJavaClass jc = new AndroidJavaClass("com.shephertz.app42.push.plugin.App42UnityHelper")) { 
						return jc.CallStatic<string> ("getLastMessage");
				}
		}
		public static void setListener(string gameObjectName) {
			if(Application.platform != RuntimePlatform.Android) return;
			AndroidJNIHelper.debug = false; 
			using (AndroidJavaClass jc = new AndroidJavaClass("com.shephertz.app42.push.plugin.App42UnityHelper")) { 
				jc.CallStatic("setListener", gameObjectName);
			}
        }

	#endif

}
	