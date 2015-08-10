package com.mohamadamin.fastsearch.free.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.dynamixsoftware.ErrorAgent;
import com.mohamadamin.fastsearch.free.databases.ApplicationsDB;

public class PackageInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationsDB applicationsDB = new ApplicationsDB(context);
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().
                    getApplicationInfo(intent.getData().getEncodedSchemeSpecificPart(), 0);
            applicationsDB.addApplication(applicationInfo);

        } catch (PackageManager.NameNotFoundException exception) {
            ErrorAgent.reportError(exception, "PackageInstallReceiver");
        }
    }
}
