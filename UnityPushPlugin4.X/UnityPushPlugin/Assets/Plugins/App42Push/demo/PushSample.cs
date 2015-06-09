using UnityEngine;
using System.Collections;
using System.IO;

using System;
using System.Net;
using AssemblyCSharpfirstpass;
using com.shephertz.app42.paas.sdk.csharp;
using com.shephertz.app42.paas.sdk.csharp.pushNotification;

public class PushSample: MonoBehaviour, App42NativePushListener
{
	private string message="App42 Push Message";
	
	private int viewHeight = Screen.height / 10;//48
	private int viewWidth = (Screen.width *3)/4 ;
	private int leftGap = Screen.width / 10;
	private int fontSize=Screen.width / 20;
	private string userName = "";
	private string myMsg = "Hi I am using Push App42";
	public string app42Response="";
	public const string ApiKey ="Your API Key";
	public const string SecretKey="Your Secret Key";
	public const string GoogleProjectNo="Your GoogleProject No";
	public const string UserId="YourUserName"; 
   
	void OnGUI()
	{
		GUIStyle style=new GUIStyle();
		style.fontSize = fontSize;
		
		GUI.Label(new Rect (leftGap, viewHeight, viewWidth, viewHeight*2), message,style);

		app42Response=Callback.response;
		app42Response = GUI.TextField (new Rect (leftGap, viewHeight*3, viewWidth, viewHeight*2),app42Response);
		userName = GUI.TextField (new Rect (leftGap, viewHeight*5, viewWidth, viewHeight),userName);
		myMsg = GUI.TextField (new Rect (leftGap, viewHeight*6, viewWidth, viewHeight),myMsg);
		//userName = GUI.TextField (new Rect (5, 150, 300, 30),userName);
		//myMsg = GUI.TextField (new Rect (5, 190, 300, 40),myMsg);
		
		if (GUI.Button(new Rect (leftGap, viewHeight*7, viewWidth, viewHeight), "Send Push To User"))
		{
			sendPushToUser(userName,myMsg);
		}
		if (GUI.Button(new Rect (leftGap, viewHeight*8, viewWidth, viewHeight), "Send Push To All"))
		{
			sendPushToAll(myMsg);
		}

	}

	public void Start (){
		DontDestroyOnLoad (transform.gameObject);
		App42API.Initialize(ApiKey,SecretKey);
		App42API.SetLoggedInUser(UserId);
		//Put Your Game Object Here
		App42Push.setApp42PushListener (this);
		#if UNITY_ANDROID
		App42Push.registerForPush (GoogleProjectNo);
		message=App42Push.getLastPushMessage();
		#endif 
	}

	public void onDeviceToken(String deviceToken){
		message="Device token from native: "+deviceToken;
		String deviceType = App42Push.getDeviceType ();
		if(deviceType!=null&&deviceToken!=null&& deviceToken.Length!=0)
			App42API.BuildPushNotificationService().StoreDeviceToken(App42API.GetLoggedInUser(),deviceToken,
                                                    deviceType,new Callback());
   }
	public void onMessage(String msg){
		message="Message From native: "+msg;
		
	}
		public void onError(String error){
		message="Error From native: "+error;
		
	}
	public void sendPushToUser(string userName,string msg){
		
		App42API.BuildPushNotificationService().SendPushMessageToUser(userName,msg,new Callback());
		
	}
	public void sendPushToAll(string msg){
		
		App42API.BuildPushNotificationService().SendPushMessageToAll(msg,new Callback());
		
	}
}


