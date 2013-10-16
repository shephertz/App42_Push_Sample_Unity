using UnityEngine;
using System.Collections;
using System.IO;

public class PushSample: MonoBehaviour
{
	Constants constants = new Constants ();
	string message = "No Message yet";
	private AndroidJavaObject testobj = null;
	private AndroidJavaObject playerActivityContext = null;
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
		

		 object[] args1 = new object[]{constants.projectNo};
		 object[] args0 = new object[]{constants.apiKey,constants.secretKey};
		 object[] args2= new object[]{constants.userId};
          object[] args3 = new object[]{constants.callBackMethod,constants.gameObjectName};
		     if (testobj == null) {
          using (var actClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer")) {
                playerActivityContext = actClass.GetStatic<AndroidJavaObject>("currentActivity");
            
		
		     using (var pluginClass = new AndroidJavaClass("com.shephertz.app42.android.pushservice.App42Service")) {
                if (pluginClass != null) {
                    testobj = pluginClass.CallStatic<AndroidJavaObject>("instance",playerActivityContext);
					testobj.Call("intialize",args0);
				    testobj.Call("setProjectNo",args1);
				    testobj.Call("setCurrentUser",args2);
                    testobj.Call("registerForNotification",args3);
                }
            }
                
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
	