package com.clubmycab.utility;

public class Log {
	
	public static void d(String tag, String logMessage) {
        if (GlobalVariables.LOGGING_ENABLED) {
            android.util.Log.d(tag, logMessage);
        }
    }
	
	public static void e(String tag, String logMessage) {
        android.util.Log.e(tag, logMessage);
    }

}
