package com.projects.owner.camlocation.Utils;

import android.content.SharedPreferences;

public class SharedPrefs {
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String NUMBER = "NUMBER";
    public static final String CURRENT_TAB = "CURRENT_TAB";
    public static final String ISLOGGEDIN = "ISLOGGEDIN";
    public static final String C_ID = "C_ID";
    public static final String C_NAME = "C_NAME";
    public static final String C_MOBILE = "C_MOBILE";
    public static final String C_EMAIL = "C_EMAIL";
    public static final String PREF_NAME = "PREF_GoTukxi_app";
    public static final String ANDROID_ID = "ANDROID_ID";
    public static final String BOOKING_SERVICE_STATUS = "BOOKING_SERVICE_STATUS";
    public static final String BOOKING_SERVICE_STATUS_RESPONSE = "BOOKING_SERVICE_STATUS_RESPONSE";
    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String DESTINATION = "DESTINATION";
    public static final String LAT = "LAT";
    public static final String LNG = "LNG";
    public static final String MOBILE = "MOBILE";
    public static final String CITIZANSHIP_NUMBER = "CITIZANSHIP_NUMBER";
    public static final String V_BRAND = "V_BRAND";
    public static final String V_MODEL = "V_MODEL";
    public static final String MILLEAGE = "MILLEAGE";
    public static final String LICENSE_NUMBER = "LICENSE_NUMBER";
    public static final String VEHICLE_NUMBER = "VEHICLE_NUMBER";
    public static final String VEHICLE_DESCRIPTION = "VEHICLE_DESCRIPTION";
    public static final String NAME = "NAME";
    public static final String CNIC = "CNIC";
    public static final String STATUS_ADD_CAB = "STATUS_ADD_CAB";
    public static final String STATUS_ADD_RV = "STATUS_ADD_RV";
    public static final String STATUS_ADD_RR = "STATUS_ADD_RR";
    public static final String ADD_R_ADDRESS = "ADD_R_ADDRESS";
    public static final String GUESTS_R_ADD = "GUESTS_R_ADD";
    public static final String BEDS_R_ADD = "BEDS_R_ADD";
    public static final String BEDROOMS_R_ADD = "BEDROOMS_R_ADD";
    public static final String BATH_R_ADD = "BATH_R_ADD";
    public static final String TITLE_TEXT = "TITLE_TEXT";

//    public static SharedPrefs getInstance(Context context)
//    {
//
//    }


    public static int getIntPref(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getInt(key, 0);
    }
    public static void StoreIntPref(SharedPreferences sharedPreferences, String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getStringPref(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getString(key, "");
    }

    public static void StoreBooleanPref(SharedPreferences sharedPreferences, String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBooleanPref(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static void StoreStringPref(SharedPreferences sharedPreferences, String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();


    }

    public static float getFloatPref(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getFloat(key, 0);
    }
    public static void StoreFloatPref(SharedPreferences sharedPreferences, String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

}