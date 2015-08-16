package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.mohamadamin.fastsearch.free.modules.CustomFile;
import com.mohamadamin.fastsearch.free.modules.CustomMusic;

import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static List<CustomFile> filterImages(Context context, String filter) {

        List<CustomFile> list = new ArrayList<>();
        CustomFile customFile;

        String where = MediaStore.Images.Media.TITLE + " LIKE ?";
        String[] params = new String[] {"%"+filter+"%"};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA},
                where,
                params,
                null
        );

        while (cursor.moveToNext()) {
            customFile = new CustomFile();
            customFile.fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            customFile.name = FileUtils.getFileName(customFile.fullPath);
            customFile.directory = FileUtils.getFileParent(customFile.fullPath);
            list.add(customFile);
        }

        cursor.close();
        return list;

    }

}
