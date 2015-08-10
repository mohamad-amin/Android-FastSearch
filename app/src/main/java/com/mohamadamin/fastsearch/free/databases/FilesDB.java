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

public class FilesDB extends SQLiteOpenHelper {

	final static int DATABASE_VERSION = 10;
	final static String DATABASE_NAME = "Files.db",
			 			TABLE_NAME = "AllFiles",
			            COLUMN_NAME = "Name",
			            COLUMN_PATH = "Directory",
						COLUMN_FULL_PATH = "FullPath",
			            COLUMN_ID = "Id";
	
	final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_NAME + " TEXT," 
            + COLUMN_PATH + " TEXT,"
			+ COLUMN_FULL_PATH + " TEXT,"
    		+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ")";

	public FilesDB(Context context) {
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

	public void deleteRecords() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		close();
	}

	public void updateFile(CustomFile customFile) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, customFile.name);
		values.put(COLUMN_PATH, customFile.directory);
		values.put(COLUMN_FULL_PATH, customFile.fullPath);
		db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(customFile.id)});
		if (db.isOpen()) db.close();
	}

	public String getInsertSql() {
		return String.format(
				"insert into %s (%s, %s, %s) values (?, ?, ?);",
				TABLE_NAME,
				COLUMN_NAME,
				COLUMN_PATH,
				COLUMN_FULL_PATH);
	}

	public void addFile(File file) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, file.getName());
		values.put(COLUMN_PATH, file.getParent());
		values.put(COLUMN_FULL_PATH, file.getAbsolutePath());
		db.insert(TABLE_NAME, null, values);
		db.close();
	}
	
	public List<CustomFile> getFiles(String name) {

		SQLiteDatabase db = this.getWritableDatabase();
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
		db.close();

		return customFiles;
		
	}

	public int getCount() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
		int count = cursor.getCount();
		cursor.close();
		if (db.isOpen()) db.close();
		return count;
	}

	public boolean deleteFile(String fullPath) {
		SQLiteDatabase db = this.getWritableDatabase();
		int result = db.delete(TABLE_NAME, COLUMN_FULL_PATH + " = ?", new String[]{fullPath});
		if (db.isOpen()) db.close();
		return (result > 0);
	}
	
	public void deleteFilesFromDirectory(String fullPath) {
		SQLiteDatabase db = this.getWritableDatabase();
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
		db.close();
	}

}
