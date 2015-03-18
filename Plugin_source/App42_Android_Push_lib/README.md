App42_Push_Unity_Lib
====================

 
App42 Push Notification Library Project for Unity Plugin.


# Building Project 

1. Download this library project from [here] (https://github.com/shephertz/App42_Push_Unity_Lib/archive/master.zip)
2. Import this project in your eclipse (make it library project if its not set).
4. Change\Refactor the package name of android library project source with your desired game package name. Default pacakge name of this library project is com.shephertz.app42.android.pushservice . 
5. While refactoring package name select `Update fully qualified names in non-Java text files (Forces Preview)` option. This will modify AndroidManifest.xml file too with new package name.
5. Cross check AndroidManifest.xml file for changes in pacakage name, if not done, do it manually.
6. Build your library project.
7. Copy App42PushService.jar and AndroidManifest.xml file from your bin folder of library Project folder and replace/paste it into Assets\plugins\Android of your Unity project.

