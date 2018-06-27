package com.projects.owner.camlocation.Utils;

import android.util.Log;

public class Logger {
    private static String TAG = "testtaxiapp ::: ";
    private static boolean log = true;
    public static void log(String message){
        if (log){
            Log.e(TAG,message);
        }
    }
}
