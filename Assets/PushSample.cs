using UnityEngine;
using System.Collections;
using System.IO;

public class PushSample: MonoBehaviour
{
	Constants constants = new Constants ();
	string message="No Message yet";
#if UNITY_ANDROID
	void OnGUI()
    {
        
		 if (GUI.Button(new Rect(50, 300, 200, 50), "EXit game"))
        {
			 Application.Quit();
		}
		  GUI.Label(new Rect (50, 200, 300, 50), message);
	}
	
	void Start (){
		GameObject androidtest = GameObject.Find(constants.gameObjectName);
			DontDestroyOnLoad(transform.gameObject);
			this.gameObject.name = constants.gameObjectName;
		RegisterForPush();
			
	}
	
	
	
	public void RegisterForPush(){
		
    Debug.Log("Sending register request... " );
		 object[] args1 = new object[]{constants.projectNo};
		 object[] args0 = new object[]{constants.apiKey,constants.secretKey};
		 object[] args2= new object[]{constants.userId};
          object[] args3 = new object[]{constants.callBackMethod,constants.gameObjectName};
          using (AndroidJavaClass cls_obj= new AndroidJavaClass("com.unity3d.player.UnityPlayer")) {
          using (AndroidJavaObject act_Obj = cls_obj.GetStatic<AndroidJavaObject>("currentActivity")) {
				act_Obj.Call("intialize",args0);
				act_Obj.Call("setProjectNo",args1);
				act_Obj.Call("setCurrentUser",args2);
                act_Obj.Call("registerForNotification",args3);
          }
    
      }
	}
		
public void Success(string val) {
if(val != null) {
	Debug.Log("Push Message is Here: " + val);
			message=val;
}
}
	
#endif
}
	