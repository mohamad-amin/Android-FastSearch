package com.mohamadamin.fastsearch.free.databases;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mohamadamin.fastsearch.free.modules.CustomApplication;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsDB extends SQLiteOpenHelper {

	PackageManager packageManager;
	SQLiteDatabase db;

	final static int DATABASE_VERSION = 10;
	public final static String DATABASE_NAME = "Applications.db",
			TABLE_NAME = "AllApps",
			COLUMN_NAME = "Name",
			COLUMN_PACKAGE = "Package";

	final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
			+ COLUMN_NAME + " TEXT,"
			+ COLUMN_PACKAGE + " TEXT" + ")";

	public ApplicationsDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		packageManager = context.getPackageManager();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public String getInsertSql() {
		return String.format(
				"insert into %s (%s, %s) values (?, ?);",
				TABLE_NAME,
				COLUMN_NAME,
				COLUMN_PACKAGE);
	}

	public void addApplication(ApplicationInfo applicationInfo) {
		if (db == null) db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, applicationInfo.loadLabel(packageManager).toString());
		values.put(COLUMN_PACKAGE, applicationInfo.packageName);
		db.insert(TABLE_NAME, null, values);
	}

	public List<CustomApplication> filterApplications(String filter) {

		if (db == null) db = getWritableDatabase();
		List<CustomApplication> customApplications = new ArrayList<>();
		CustomApplication customApplication;

		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
				COLUMN_NAME + " LIKE '%" + filter + "%'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				customApplication = new CustomApplication();
				customApplication.titleText = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
				customApplication.packageName = cursor.getString(cursor.getColumnIndex(COLUMN_PACKAGE));
				customApplications.add(customApplication);
			}
		}

		cursor.close();
		return customApplications;

	}

	public Cursor getFilteredCursor(String filter) {
		if (db == null) db = getWritableDatabase();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
				COLUMN_NAME + " LIKE '%" + filter + "%'";
		return db.rawQuery(query, null);
	}

	public void removeApplication(String packageName) {
		if (db == null) db = getWritableDatabase();
		db.delete(TABLE_NAME, COLUMN_PACKAGE + " = ?", new String[]{packageName});
	}

}
