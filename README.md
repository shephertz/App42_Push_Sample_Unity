App42_Push_Sample_Unity
=======================

# About Application

1. This application shows how can we integrate our Unity3D game with Push Notification using android Framework.
2. Once a game registered for Push Notification Unity3D game get Notification message accordingly.

# Running Sample

This is a sample Android Unity 3D app is made by using App42 back-end platform. It uses Push Notification of App42 platform.
Here are the few easy steps to run this sample app.

1. [Register] (https://apphq.shephertz.com/register) with App42 platform.
2. Create an app once you are on Quick start page after registration.
3. If you are already registered, login to [AppHQ] (http://apphq.shephertz.com) console and create an app from App Manager Tab.
4. To use PushNotification service in your application,create a new project in [Google API Console] (https://cloud.google.com/console/project).
5. Click on services option in Google console and enable Google Cloud Messaging for Android service.
6. Click on API Access tab and create a new server key for your application with blank server information.
7. Go to [AppHQ] (http://apphq.shephertz.com) console and click on Push Notification and select Android setting in Settings option.
8. Select your app and copy server key that is generated by using Google API console, and submit it.
9. Download the Unity project from [here] (https://github.com/shephertz/App42_Push_Sample_Unity/archive/master.zip). It contains PluginSource of Android Native,Unity Project and Unity Package of plugin.
10. If you want integrate in your existing Unity project import App42UnityPushPlugin package by selecting custom asset import option in Assets.
11. Attach PushSample.cs file on Main Camera if not attached and make following changes.

```
A. Change ApiKey and SecretKey that you have received in step 2 or 3 at line no 22 and 23.
B. Change GoogleProjectNo with your Google Project Number  at line no 24.
C. Change UserId with your username you want tp register for Push Notification at line no 25..
```
Step-12 : Change "com.test.push" sample package Name with you Bundle Identifier in AndroidManifest.xml file of Android\plugin 
folder.
13.Build your android application and run it on your device.
14. All plugin source written in App42Push.cs file.

__Test and verify Push Notification__
```
A. After registering for Push Notification go to AppHQ console and click on Push Notification and select application after selecting User tab.
B. Select desired user from registered UserList and click on Send Message Button.
C. Send appropriate message to user by clicking Send Button.
D. Now you will get same message on your android device and your callback Method of Unity3D.
```

__System Requirements:__
```
A. Unity 3D.
B. Android SDK with 4.0 API .
```

4. Modify AndroidManifest.xml file with your AndroidManifest.xml accordingly.


# Design Details:

__1. Android Configurations:__ You can also configure AndroidManifest accordingly. 

  1.1 Replace fully qualified name "com.test.push" with your bundle identifier it AndroidManifest.xml in Android plug-in folder.
  1.2 You can also change Push Notification title by replacing "App42PushUnity" with your title at line no 64 in AndroidManifest.xml file.
  1.3 Ensure your Android plug-in folder contains android_support and googlePlayService jar files from plug-in.
  1.3 If you are creating you own AndroidManifest.xml file add following things from plug-in AndroidManifest.xml file.
    1.3.1 Add following permissions in AndroidManifest.xml file, also change "com.test.push" with you android Bundle Identifier or package name.
```
        <permission
				android:name="com.test.push.permission.C2D_MESSAGE"
				android:protectionLevel="signature" />
				<uses-permission android:name="com.test.push.permission.C2D_MESSAGE" />
				<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
				<uses-permission android:name="android.permission.WAKE_LOCK" />
				<uses-permission android:name="android.permission.INTERNET" />
				<uses-permission android:name="android.permission.VIBRATE" />
				 <uses-permission android:name="android.permission.GET_ACCOUNTS" />
```
      1.3.2 Add App42GCMService :
	  ```
	   <service android:name="com.shephertz.app42.push.plugin.App42GCMService" > 
			  </service>
	 ```
	  1.3.2 Add App42GCMReceiver :
	  ```
	   <receiver android:name="com.shephertz.app42.push.plugin.App42GCMReceiver"
					android:permission="com.google.android.c2dm.permission.SEND" >
					<intent-filter>
						<!-- Receives the actual messages. -->
						<action android:name="com.google.android.c2dm.intent.RECEIVE" />
						<!-- Receives the registration id. -->
						<action android:name="com.google.android.c2dm.intent.REGISTRATION" />
						<!-- For customize with your app change below line 51 "com.test.push".with your app package name-->
						<category android:name="com.test.push" />
					</intent-filter>
				</receiver>
	 ```
	  1.3.4 Add Meta data Info :
	  ```
	  <meta-data android:name="push_title" android:value="App42PushUnity"/>
				<meta-data android:name="com.google.android.gms.version" android:value="6587000" />
				<meta-data android:name="onMessageOpen"
					android:value="com.unity3d.player.UnityPlayerActivity" />
	  ```
__2. App42 Unity Push Integration:__ All work related to Push Integration and receiving in written in App42Push.cs file. 

           2.1 Add following code in Start() function of your script file :
		  
		      ```
				App42API.Initialize("App42 API Key","App42 Secret Key");
				//which you want to register for Push Notification
				App42API.SetLoggedInUser("App42 userId");
				//prior implement App42NativePushListener in you script file
				App42Push.setApp42PushListener (this);
				#if UNITY_ANDROID
				App42Push.registerForPush ("Your Google Project No");
				message=App42Push.getLastPushMessage();
				#endif 
			    ```
		   2.2	 Add following callBack in script file as well :
		      
			 ```
			    public void onDeviceToken(String deviceToken){
					Debug.Log ("Device token from native: "+deviceToken);
					String deviceType = App42Push.getDeviceType ();
					if(deviceType!=null&&deviceToken!=null&& deviceToken.Length!=0)
						App42API.BuildPushNotificationService().StoreDeviceToken(App42API.GetLoggedInUser(),deviceToken,
                                                    deviceType,new Callback());
				}
				
			    public void onMessage(String msg){
				 Debug.Log ("Message From native: "+msg);
				}
				public void onError(String error){
					Debug.Log ("Error From native: "+error);
				}
			 ```
	  
__3. Send PushNotification to User using Unity App42 SDK :__ You can use this method written in PushSample..cs file.
 
```
	public void sendPushToUser(string userName,string msg){
		App42API.BuildPushNotificationService().SendPushMessageToUser(userName,msg,new Callback());
	}

```

__4 Send PushNotification to all users using Unity App42 SDK :__ You can use this method written in PushSample..cs file..
 
```
	 public void sendPushToAll(string msg){	
		 App42API.BuildPushNotificationService().SendPushMessageToAll(msg,new Callback());
        }

```

```
# Integration and Customization in Your Existing Project.

__Integration in your Existing project__ If you want to integrate it in your existing unity project import App42UnityPushPlugin package as a custom asset in your project and follow all necessary steps mentioned above.

__Configuring Notification UI__ 

If you want to customize Notification UI,you can modify import plugin source in your Eclipse IDE.

```
Notification notification = new NotificationCompat.Builder(context)
				.setContentTitle(ServiceContext.instance(context).getGameObject())
				.setContentText(message).
				 setContentIntent(intent)
				.setSmallIcon(android.R.drawable.ic_dialog_info)
				.setWhen(when)
				.setNumber(++msgCount)
				.setLargeIcon(getBitmapFromAssets())
				.setLights(Color.YELLOW, 1, 2)
				.setAutoCancel(true).build();

		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
```

--> Build your library project.

--> Copy app42pushservice.jar from your bin folder of library Project folder and replace/paste it into Assets\plugins\Android of your Unity project.

__Don't Show Push If Application is Opened __ If you don't want to show notificaation when application do following configurations.

1. Copy app42pushserviceV2.jar file from App42_Plugin_Jar folder and replcae it with app42pushservice.jar file into  Assets\plugins\Android folder of your unity project.
2. Add Permission 
  <uses-permission android:name="android.permission.GET_TASKS" />
  
__Supported Messages  __
1. You can send Text message.
2. You can send Json message as well like

{"message":"Notification message","key1":""value","key2":"value"
}
3. You can configure Push title by changing the "push_title" value in AndroidManifest.xml file.
For more info go through with "http://api.shephertz.com/app42-docs/push-notification-service/?sdk=unity"
