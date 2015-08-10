package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.webkit.MimeTypeMap;

import com.mohamadamin.fastsearch.free.databases.DirectoriesDB;
import com.mohamadamin.fastsearch.free.databases.FilesDB;
import com.mohamadamin.fastsearch.free.modules.CustomFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    SQLiteStatement filesStatement, directoriesStatement;
    SQLiteDatabase filesDatabase, directoriesDatabase;
    FilesDB filesDB;
    DirectoriesDB directoriesDB;
    Context context;
    String fileName, fileParent;

    public FileUtils(Context context) {
        this.context = context;
    }

    public void openDatabases() {
        filesDB = new FilesDB(context);
        directoriesDB = new DirectoriesDB(context);
        filesDatabase = filesDB.getWritableDatabase();
        directoriesDatabase = directoriesDB.getWritableDatabase();
        filesDatabase.beginTransaction();
        directoriesDatabase.beginTransaction();
        filesStatement = filesDatabase.compileStatement(filesDB.getInsertSql());
        directoriesStatement = directoriesDatabase.compileStatement(directoriesDB.getInsertSql());
    }

    public void closeDatabases() {
        filesDatabase.setTransactionSuccessful();
        directoriesDatabase.setTransactionSuccessful();
        filesDatabase.endTransaction();
        directoriesDatabase.endTransaction();
        filesDB.close();
        directoriesDB.close();
    }

    public static String getExtensionFromFilePath(String fullPath) {
        String filenameArray[] = fullPath.split("\\.");
        return filenameArray[filenameArray.length-1];
    }

    public static String getMimeTypeFromFilePath(String filePath) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtensionFromFilePath(filePath));
        return (mimeType == null) ? "*/*" : mimeType;
    }

    public static List<File> getStorageFiles() {
        List<File> files = new ArrayList<>();
        files.add(new File(System.getenv("EXTERNAL_STORAGE")));
        try {
            for (String path : System.getenv("SECONDARY_STORAGE").split(":")) {
                File file = new File(path);
                if (file.exists()) files.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public void addFilesToDatabase(File directory) {

        File[] chileFiles = directory.listFiles();

        if (chileFiles != null) {
            for(File file : chileFiles) {
                if (file.isDirectory()) {
                    if (!file.getAbsolutePath().contains("/Android/data") && !file.getAbsolutePath().contains("/Android/obb")) {
                        fileName = file.getName();
                        fileParent = file.getParent()+"/";
                        directoriesStatement.bindString(1, fileName);
                        directoriesStatement.bindString(2, fileParent);
                        directoriesStatement.bindString(3, fileParent + fileName);
                        directoriesStatement.executeInsert();
                        directoriesStatement.clearBindings();
                        addFilesToDatabase(file);
                    }
                } else {
                    fileName = file.getName();
                    fileParent = file.getParent()+"/";
                    filesStatement.bindString(1, fileName);
                    filesStatement.bindString(2, fileParent);
                    filesStatement.bindString(3, fileParent + fileName);
                    filesStatement.executeInsert();
                    filesStatement.clearBindings();
                }
            }
        }

    }

    public static void deleteFile(CustomFile customFile) {
        File file = new File(customFile.fullPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void renameFile(CustomFile oldFile, String newName) {
        File file = new File(oldFile.fullPath);
        if (file.exists()) {
            File to = new File(file.getParent(), newName);
            file.renameTo(to);
        }
        oldFile.name = newName;
    }

}
