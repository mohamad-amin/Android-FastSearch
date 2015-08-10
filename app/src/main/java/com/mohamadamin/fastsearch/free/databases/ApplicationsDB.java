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
	final static int DATABASE_VERSION = 10;
	final static String DATABASE_NAME = "Applications.db",
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
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, applicationInfo.loadLabel(packageManager).toString());
		values.put(COLUMN_PACKAGE, applicationInfo.packageName);
		db.insert(TABLE_NAME, null, values);
		db.close();
	}

	public void deleteRecords() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		close();
	}

	public List<CustomApplication> filterApplications(String filter) {

		List<CustomApplication> customApplications = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
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
		db.close();

		return customApplications;

	}

	public int getCount() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
		int count = cursor.getCount();
		cursor.close();
		if (db.isOpen()) db.close();
		return count;
	}

	public void removeApplication(String packageName) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, COLUMN_PACKAGE + " = ?", new String[]{packageName});
		if (db.isOpen()) db.close();
	}

}
