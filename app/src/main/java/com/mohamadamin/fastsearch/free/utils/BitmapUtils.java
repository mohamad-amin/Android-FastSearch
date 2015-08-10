package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtils {

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;

    }

    public static Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {

        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

    }

    public static Bitmap getResizedBitmapChecked(Bitmap bitmap, Context context) {

        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = context.getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);

        if (width > height) {
            if (width > size) {
                float scale = size/width;
                return getResizedBitmap(bitmap, (int) (width*scale), (int) (height*scale));
            } else return bitmap;
        } else if (height > width) {
            if (height > size) {
                float scale = size/height;
                return getResizedBitmap(bitmap, (int) (width*scale), (int) (height*scale));
            } else return bitmap;
        } else {
            if (width > size) return getResizedBitmap(bitmap, size, size);
            else return bitmap;
        }

    }

}
