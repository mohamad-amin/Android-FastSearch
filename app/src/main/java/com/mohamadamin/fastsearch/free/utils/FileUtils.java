package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.mohamadamin.fastsearch.free.modules.CustomFile;

import java.io.File;

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
        File file = (TextUtils.isEmpty(customFile.fullPath)) ?
                new File(customFile.audioPath) : new File(customFile.fullPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void renameFile(CustomFile oldFile, String newName) {
        File file = (TextUtils.isEmpty(oldFile.fullPath)) ?
                new File(oldFile.audioPath) : new File(oldFile.fullPath);
        if (file.exists()) {
            File to = new File(file.getParent(), newName);
            file.renameTo(to);
        }
    }

}
