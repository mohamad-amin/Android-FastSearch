package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.mohamadamin.fastsearch.free.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;
import java.util.HashMap;

public class PicassoUtils {

    public static final String SCHEME_VIDEO = "picassoVideo",
            SCHEME_CONTACT = "picassoContact",
            SCHEME_APK = "picassoApk",
            SCHEME_AUDIO = "picassoAudio",
            SCHEME_FILE = "picassoFile",
            SCHEME_APPLICATION = "picassoApplication";

    public static class VideoRequestHandler extends RequestHandler {

        Context context;

        public VideoRequestHandler(Context context) {
            super();
            this.context = context;
        }

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_VIDEO.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
            if (bitmap == null) bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_video);
            return new Result(BitmapUtils.getResizedBitmapChecked(bitmap, context), Picasso.LoadedFrom.DISK);
        }

    }

    public static class ContactRequestHandler extends RequestHandler {

        Context context;

        public ContactRequestHandler(Context context) {
            super();
            this.context = context;
        }

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_CONTACT.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), Uri.parse(data.uri.getPath().substring(1)));
            if (bitmap == null) bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_contact);
            return new Result(BitmapUtils.getResizedBitmapChecked(bitmap, context), Picasso.LoadedFrom.DISK);
        }

    }

    public static class ApkRequestHandler extends RequestHandler {

        Context context;

        public ApkRequestHandler(Context context) {
            super();
            this.context = context;
        }

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_APK.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_android);
            PackageInfo packageInfo = context.getPackageManager().
                    getPackageArchiveInfo(data.uri.getPath(), PackageManager.GET_ACTIVITIES);
            if(packageInfo != null) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                applicationInfo.sourceDir = data.uri.getPath();
                applicationInfo.publicSourceDir = data.uri.getPath();
                bitmap = BitmapUtils.getBitmapFromDrawable(
                        applicationInfo.loadIcon(context.getPackageManager()));
            }
            return new Result(BitmapUtils.getResizedBitmapChecked(bitmap, context), Picasso.LoadedFrom.DISK);
        }

    }

    public static class AudioRequestHandler extends RequestHandler {

        Context context;

        public AudioRequestHandler(Context context) {
            super();
            this.context = context;
        }

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_AUDIO.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bitmap;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(data.uri.getPath());
            byte[] bytes = retriever.getEmbeddedPicture();
            if (bytes != null) bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            else bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_music);
            return new Result(BitmapUtils.getResizedBitmapChecked(bitmap, context), Picasso.LoadedFrom.DISK);
        }

    }

    public static class FileRequestHandler extends RequestHandler {

        Context context;
        HashMap<String, Integer> resourcePictures;

        public FileRequestHandler(Context context) {
            this.context = context;
            this.resourcePictures = getFilePictures();
        }

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_FILE.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bitmap;
            String extension = data.uri.getPath();
            if (resourcePictures.containsKey(extension)) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), resourcePictures.get(extension));
            } else bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_unknown);
            return new Result(BitmapUtils.getResizedBitmapChecked(bitmap, context), Picasso.LoadedFrom.DISK);
        }

    }

    public static class ApplicationRequestHandler extends RequestHandler {

        Context context;

        public ApplicationRequestHandler(Context context) {
            this.context = context;
        }

        @Override
        public boolean canHandleRequest(Request data) {
            String scheme = data.uri.getScheme();
            return (SCHEME_APPLICATION.equals(scheme));
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bitmap;
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(data.uri.getPath().substring(1), 0);
                bitmap = BitmapUtils.getBitmapFromDrawable(
                        applicationInfo.loadIcon(packageManager));
            } catch (PackageManager.NameNotFoundException exception) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.file_android);
            }
            return new Result(BitmapUtils.getResizedBitmapChecked(bitmap, context), Picasso.LoadedFrom.DISK);
        }

    }


    private static HashMap<String, Integer> getFilePictures() {

        HashMap<String, Integer> resourcePictures = new HashMap<>();

        resourcePictures.put("zip", R.drawable.file_zip);
        resourcePictures.put("rar", R.drawable.file_zip);
        resourcePictures.put("7zip", R.drawable.file_zip);
        resourcePictures.put("tar", R.drawable.file_zip);
        resourcePictures.put("7z", R.drawable.file_zip);
        resourcePictures.put("gz", R.drawable.file_zip);
        resourcePictures.put("gzip", R.drawable.file_zip);

        resourcePictures.put("pdf", R.drawable.file_adobe);
        resourcePictures.put("epub", R.drawable.file_adobe);
        resourcePictures.put("rzb", R.drawable.file_adobe);
        resourcePictures.put("azw", R.drawable.file_adobe);
        resourcePictures.put("mobi", R.drawable.file_adobe);
        resourcePictures.put("book", R.drawable.file_adobe);
        resourcePictures.put("indd", R.drawable.file_adobe);

        resourcePictures.put("mp3", R.drawable.file_music);
        resourcePictures.put("wav", R.drawable.file_music);
        resourcePictures.put("ogg", R.drawable.file_music);
        resourcePictures.put("m4a", R.drawable.file_music);
        resourcePictures.put("aif", R.drawable.file_music);

        resourcePictures.put("mp4", R.drawable.file_video);
        resourcePictures.put("wma", R.drawable.file_video);
        resourcePictures.put("avi", R.drawable.file_video);
        resourcePictures.put("mpg", R.drawable.file_video);
        resourcePictures.put("wma", R.drawable.file_video);
        resourcePictures.put("mov", R.drawable.file_video);
        resourcePictures.put("flv", R.drawable.file_video);
        resourcePictures.put("3gp", R.drawable.file_video);
        resourcePictures.put("swf", R.drawable.file_video);
        resourcePictures.put("divx", R.drawable.file_video);

        resourcePictures.put("doc", R.drawable.file_office);
        resourcePictures.put("msg", R.drawable.file_office);
        resourcePictures.put("docx", R.drawable.file_office);
        resourcePictures.put("ppt", R.drawable.file_office);
        resourcePictures.put("xls", R.drawable.file_office);
        resourcePictures.put("xlr", R.drawable.file_office);
        resourcePictures.put("xlsx", R.drawable.file_office);
        resourcePictures.put("ppt", R.drawable.file_office);
        resourcePictures.put("pps", R.drawable.file_office);
        resourcePictures.put("pub", R.drawable.file_office);
        resourcePictures.put("mdb", R.drawable.file_office);

        resourcePictures.put("psd", R.drawable.file_photoshop);
        resourcePictures.put("ai", R.drawable.file_photoshop);

        resourcePictures.put("txt", R.drawable.file_text);
        resourcePictures.put("log", R.drawable.file_text);
        resourcePictures.put("jar", R.drawable.file_text);
        resourcePictures.put("java", R.drawable.file_text);
        resourcePictures.put("class", R.drawable.file_text);
        resourcePictures.put("xml", R.drawable.file_text);

        resourcePictures.put("html", R.drawable.file_web);
        resourcePictures.put("css", R.drawable.file_web);
        resourcePictures.put("csr", R.drawable.file_web);
        resourcePictures.put("cfm", R.drawable.file_web);
        resourcePictures.put("js", R.drawable.file_web);
        resourcePictures.put("jsp", R.drawable.file_web);
        resourcePictures.put("rss", R.drawable.file_web);
        resourcePictures.put("xhtml", R.drawable.file_web);
        resourcePictures.put("php", R.drawable.file_web);
        resourcePictures.put("htm", R.drawable.file_web);
        resourcePictures.put("asp", R.drawable.file_web);
        resourcePictures.put("aspx", R.drawable.file_web);

        resourcePictures.put("jpg", R.drawable.file_image);
        resourcePictures.put("JPG", R.drawable.file_image);
        resourcePictures.put("bmp", R.drawable.file_image);
        resourcePictures.put("png", R.drawable.file_image);
        resourcePictures.put("gif", R.drawable.file_image);
        resourcePictures.put("jpeg", R.drawable.file_image);
        resourcePictures.put("dng", R.drawable.file_image);

        resourcePictures.put("fla", R.drawable.file_flash);

        return resourcePictures;

    }

}
