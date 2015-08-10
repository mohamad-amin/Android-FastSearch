package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.mohamadamin.fastsearch.free.modules.CustomFileObserver;

import java.io.File;

public class RunUtils {

    public static void startServicesAndListeners(final Context context) {
        new AsyncTask<Void, Void, Void> (){
            @Override
            protected Void doInBackground(Void... params) {
                startFileObservers(context);
                return null;
            }
        }.execute();
    }

    public static void startFileObservers(Context context) {
        CustomFileObserver fileObserver;
        for (File file : FileUtils.getStorageFiles()) {
            fileObserver = new CustomFileObserver(file.getAbsolutePath(), context);
            fileObserver.startWatching();
        }
    }

}
