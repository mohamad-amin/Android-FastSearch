package com.mohamadamin.fastsearch.free.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mohamadamin.fastsearch.free.modules.CustomFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoriesDB extends SQLiteOpenHelper {

	SQLiteDatabase db;

	final static int DATABASE_VERSION = 10;
	final static String DATABASE_NAME = "Directories.db",
			 			TABLE_NAME = "AllDirectories",
			            COLUMN_NAME = "Name",
			            COLUMN_PATH = "Directory",
						COLUMN_FULL_PATH = "FullPath",
			            COLUMN_ID = "Id";

	final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_NAME + " TEXT,"
            + COLUMN_PATH + " TEXT,"
			+ COLUMN_FULL_PATH + " TEXT,"
    		+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ")";

	public DirectoriesDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
				"insert into %s (%s, %s, %s) values (?, ?, ?);",
				TABLE_NAME,
				COLUMN_NAME,
				COLUMN_PATH,
				COLUMN_FULL_PATH);
	}

	public void deleteRecords() {
		if (db == null) db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void addFile(File file) {
		if (db == null) db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, file.getName());
		values.put(COLUMN_PATH, file.getParent());
		values.put(COLUMN_FULL_PATH, file.getAbsolutePath());
		db.insert(TABLE_NAME, null, values);
	}
	
	public List<CustomFile> getFiles(String name) {

		if (db == null) db = this.getWritableDatabase();

		List<CustomFile> customFiles = new ArrayList<>();
		CustomFile customFile;
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
				COLUMN_NAME + " LIKE '%" + name + "%'";

		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				customFile = new CustomFile();
				customFile.name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
				customFile.directory = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
				customFile.fullPath = cursor.getString(cursor.getColumnIndex(COLUMN_FULL_PATH));
				customFile.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
				customFiles.add(customFile);
			}
		}

		cursor.close();
		return customFiles;
		
	}
	
	public void updateFile(CustomFile customFile) {
		if (db == null) db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, customFile.name);
		values.put(COLUMN_PATH, customFile.directory);
		values.put(COLUMN_FULL_PATH, customFile.fullPath);
		db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(customFile.id)});
	}

	public boolean deleteFile(String fullPath) {
		if (db == null) db = this.getWritableDatabase();
		int result = db.delete(TABLE_NAME, COLUMN_FULL_PATH + " = ?", new String[]{fullPath});
		return (result > 0);
	}
	
	public void deleteFilesFromDirectory(String fullPath) {
		if (db == null) db = this.getWritableDatabase();
		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +
				COLUMN_PATH + " LIKE '%" + fullPath+"/" + "%'";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{
						String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)))});
			}
		}
		cursor.close();
	}

}
