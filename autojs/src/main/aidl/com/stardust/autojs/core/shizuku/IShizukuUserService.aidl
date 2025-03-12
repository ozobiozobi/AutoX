// IShizukuUserService.aidl
package com.stardust.autojs.core.shizuku;

// Declare any non-default types here with import statements

interface IShizukuUserService {
     void destroy() = 16777114; // Destroy method defined by Shizuku server

     void exit() = 1; // Exit method defined by user

     String runShellCommand(int id, String command) = 2;
     void recycleShell(int id) = 3;
     void setConsole(in IBinder listener) = 4;

     void runNodeScript(String path) = 5;
     String runRhinoScript(String script) = 6;
     String runRhinoScriptFile(String path) = 7;

     String getPackageName() = 55; // Get package name
}