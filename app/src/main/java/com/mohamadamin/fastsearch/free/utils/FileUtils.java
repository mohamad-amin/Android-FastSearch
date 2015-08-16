package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.mohamadamin.fastsearch.free.modules.CustomFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    Context context;

    public FileUtils(Context context) {
        this.context = context;
    }

    public static String getExtensionFromFilePath(String fullPath) {
        String filenameArray[] = fullPath.split("\\.");
        return filenameArray[filenameArray.length-1];
    }

    public static String getFileName(String fullPath) {
        return fullPath.substring(fullPath.lastIndexOf(File.separator) + 1);
    }

    public static String getFileParent(String fullPath) {
        return fullPath.substring(0, fullPath.lastIndexOf(File.separator));
    }

    public static String getMimeTypeFromFilePath(String filePath) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtensionFromFilePath(filePath));
        return (mimeType == null) ? "*/*" : mimeType;
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

    public static List<CustomFile> filterFiles(Context context, String filter) {

        List<CustomFile> list = new ArrayList<>();
        CustomFile customFile;
        String path;

        String nonMediaCondition = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
        String where = nonMediaCondition + " AND " + MediaStore.Files.FileColumns.TITLE + " LIKE ?";
        String[] params = new String[] {"%"+filter+"%"};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                new String[]{MediaStore.Files.FileColumns.DATA},
                where,
                params,
                null
        );

        while (cursor.moveToNext()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            if (path != null) {
                customFile = new CustomFile();
                customFile.fullPath = path;
                customFile.name = getFileName(path);
                customFile.directory = getFileParent(path);
                list.add(customFile);
            }
        }

        cursor.close();
        return list;

    }

}
