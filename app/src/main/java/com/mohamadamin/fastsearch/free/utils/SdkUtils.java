package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import com.mohamadamin.fastsearch.free.databases.ApplicationsDB;

import java.util.List;

public class SdkUtils {

    public static boolean isPackageInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException exception) {
            return false;
        }
    }

    public static void addPackagesToDatabase(Context context) {

        ApplicationsDB applicationsDB = new ApplicationsDB(context);
        SQLiteDatabase db = applicationsDB.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement statement = db.compileStatement(applicationsDB.getInsertSql());

        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            statement.bindString(1, applicationInfo.loadLabel(packageManager).toString());
            statement.bindString(2, applicationInfo.packageName);
            statement.executeInsert();
            statement.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        if (db.isOpen()) db.close();
        applicationsDB.close();

    }

    public static boolean isJellyBeanMR1OrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean isHoneycombOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    @SuppressWarnings("deprecation")
    public static void removeLayoutListener(View anchorView, ViewTreeObserver.OnGlobalLayoutListener victim) {
        if (Build.VERSION.SDK_INT >= 16) anchorView.getViewTreeObserver().removeOnGlobalLayoutListener(victim);
        else anchorView.getViewTreeObserver().removeGlobalOnLayoutListener(victim);
    }

}
