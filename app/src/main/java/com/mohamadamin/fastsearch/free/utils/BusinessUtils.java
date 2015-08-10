package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.mohamadamin.fastsearch.free.R;

public class BusinessUtils {

    public static void launchInstagramPage(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse("http://instagram.com/_u/mohamad__amin"));
            intent.setPackage("com.instagram.android");
            context.startActivity(intent);
        } catch (Exception ignored) {
            intent.setData(Uri.parse("http://instagram.com/mohamad__amin"));
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.instagram_page)));
        }
    }

    public static void sendEmailToDeveloper(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","torpedo.mohammadi@gmail.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "[Fast Search]");
        try {
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.communication_send_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, context.getResources().getString(R.string.communication_send_mail_no_application), Toast.LENGTH_SHORT).show();
        }
    }

}
