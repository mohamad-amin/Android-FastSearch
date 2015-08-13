package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtils {
	
	public static String SHOULD_WRITE_DATABASES = "shouldWriteDatabases",
			SHOULD_SHOW_SEARCH_NOTIFICATION = "shouldShowSearchNotification";

	public static boolean shouldWriteDatabases(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(SHOULD_WRITE_DATABASES, true);
	}

	public static boolean shouldShowSearchNotification(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(SHOULD_SHOW_SEARCH_NOTIFICATION, true);
	}

	public static void setShouldWriteDatabases(Context context, boolean shouldWriteDatabases) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(SHOULD_WRITE_DATABASES, shouldWriteDatabases);
		editor.apply();
	}

	public static void setShouldShowSearchNotification(Context context, boolean shouldShowSearchNotification) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(SHOULD_SHOW_SEARCH_NOTIFICATION, shouldShowSearchNotification);
		editor.apply();
	}

}
