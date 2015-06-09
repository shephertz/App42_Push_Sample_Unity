using UnityEngine;
using System.Collections;
using com.shephertz.app42.paas.sdk.csharp;
using com.shephertz.app42.paas.sdk.csharp.pushNotification;
using System;

public class Callback : App42CallBack{

	public static string response = "";
	public void OnSuccess(object responsePush){
		response = responsePush.ToString();
		         PushNotification pushNotification = (PushNotification)responsePush;
				Debug.Log ("UserName : " + pushNotification.GetUserName());	
				Debug.Log ("Expiery : " + pushNotification.GetExpiry());
				Debug.Log ("DeviceToken : " + pushNotification.GetDeviceToken());	
				Debug.Log ("pushNotification : " + pushNotification.GetMessage());	
				Debug.Log ("pushNotification : " + pushNotification.GetStrResponse());	
				Debug.Log ("pushNotification : " + pushNotification.GetTotalRecords());	
				Debug.Log ("pushNotification : " + pushNotification.GetType());	
	}
	public void OnException(Exception ex){
		Debug.Log("EDITEOR---"+ ex.ToString());
		response=ex.ToString();
	}

}
