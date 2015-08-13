package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.mohamadamin.fastsearch.free.modules.CustomMusic;

import java.util.ArrayList;
import java.util.List;

public class MusicUtils {

    public static List<CustomMusic> filterMusics(Context context, String filter) {

        List<CustomMusic> list = new ArrayList<>();
        CustomMusic customMusic;

        String where = MediaStore.Audio.Media.TITLE + " LIKE ? OR " +
                MediaStore.Audio.Media.ALBUM + " LIKE ? OR " +
                MediaStore.Audio.Media.ARTIST + " LIKE ?";
        String[] params = new String[] {
                "%" + filter + "%",
                "%" + filter + "%",
                "%" + filter + "%"
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                where,
                params,
                MediaStore.Audio.Media.TITLE
        );

        while (cursor.moveToNext()) {
            customMusic = new CustomMusic();
            customMusic.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            customMusic.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            customMusic.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            customMusic.audioPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            list.add(customMusic);
        }

        cursor.close();
        return list;

    }

}
