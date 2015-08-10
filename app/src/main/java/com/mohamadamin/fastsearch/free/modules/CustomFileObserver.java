package com.mohamadamin.fastsearch.free.modules;

import android.content.Context;
import android.os.FileObserver;
import android.util.Log;

import com.mohamadamin.fastsearch.free.databases.DirectoriesDB;
import com.mohamadamin.fastsearch.free.databases.FilesDB;

import java.io.File;

public class CustomFileObserver extends RecursiveFileObserver {

    String basePath;
    Context context;
    FilesDB filesDB;
    DirectoriesDB directoriesDB;

    public CustomFileObserver(String  path, Context context) {
        super(path);
        this.basePath = path;
        this.context = context;
        if (!this.basePath.endsWith("/")) this.basePath += "/";
        this.filesDB = new FilesDB(context);
        this.directoriesDB = new DirectoriesDB(context);
    }

    @Override
    public void onEvent(int event, String path) {

        String fullPath = basePath + path;
        File file = new File(fullPath);

        if ((FileObserver.CREATE & event) != 0) fileCreated(file);
        else if ((FileObserver.DELETE & event) != 0) fileDeleted(fullPath);
        else if ((FileObserver.DELETE_SELF & event) != 0) fileDeleted(fullPath);
        else if ((FileObserver.MOVED_FROM & event) != 0) fileDeleted(fullPath);
        else if ((FileObserver.MOVED_TO & event) != 0) fileCreated(file);

    }

    private void fileCreated(File file) {
        if (file.exists()) {
            if (file.isDirectory()) directoriesDB.addFile(file);
            else filesDB.addFile(file);
        } else Log.d("mytag", "File: " + file.getAbsolutePath() + " created and doesn't exist!");
    }

    private void fileDeleted(String fullPath) {
        filesDB.deleteFile(fullPath);
        directoriesDB.deleteFile(fullPath);
        filesDB.deleteFilesFromDirectory(fullPath);
        directoriesDB.deleteFilesFromDirectory(fullPath);
    }

}
