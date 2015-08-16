package com.mohamadamin.fastsearch.free.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.mohamadamin.fastsearch.free.modules.CustomContact;

import java.util.ArrayList;
import java.util.List;

public class ContactUtils {

    public static List<CustomContact> filterContacts(Context context, String filter) {

        List<CustomContact> list = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        CustomContact customContact;

        String selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%"+filter+"%"};

        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                customContact = new CustomContact();
                customContact.idString = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                customContact.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                customContact.uri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                if (Integer.parseInt(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[] {customContact.idString}, null);
                    phones.moveToNext();
                    customContact.phone = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phones.close();
                }
                list.add(customContact);
            }
        }

        cursor.close();
        return list;

    }

}
