package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.mohamadamin.fastsearch.free.modules.CustomFile;

import java.util.ArrayList;
import java.util.List;

public class VideoUtils {

    public static List<CustomFile> filterVideos(Context context, String filter) {

        List<CustomFile> list = new ArrayList<>();
        CustomFile customFile;

        String where = MediaStore.Video.Media.TITLE + " LIKE ?";
        String[] params = new String[] {"%"+filter+"%"};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media.DATA},
                where,
                params,
                null
        );

        while (cursor.moveToNext()) {
            customFile = new CustomFile();
            customFile.fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            customFile.name = FileUtils.getFileName(customFile.fullPath);
            customFile.directory = FileUtils.getFileParent(customFile.fullPath);
            list.add(customFile);
        }

        cursor.close();
        return list;

    }

}
