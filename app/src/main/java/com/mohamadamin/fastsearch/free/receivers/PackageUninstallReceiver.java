package com.mohamadamin.fastsearch.free.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mohamadamin.fastsearch.free.databases.ApplicationsDB;

public class PackageUninstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationsDB applicationsDB = new ApplicationsDB(context);
        applicationsDB.removeApplication(intent.getData().getEncodedSchemeSpecificPart());
    }
}
