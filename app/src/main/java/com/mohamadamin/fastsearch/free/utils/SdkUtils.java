package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;

import com.mohamadamin.fastsearch.free.databases.ApplicationsDB;

import java.util.List;

public class SdkUtils {

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

    public static boolean isHoneycombOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

}
