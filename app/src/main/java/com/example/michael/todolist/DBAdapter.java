package com.example.michael.todolist; // change the name

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	private static final String TAG = "DBAdapter"; // useed for logging final databse version

	// Field names:
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TASK = "task";
	public static final String KEY_DATE = "date";

	public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_TASK, KEY_DATE};

	// Column numbers for each field name:
	public static final int COL_ROWID = 0;
	public static final int COL_TASK = 1;
	public static final int COL_DATE = 2;

	// DataBase info:
	public static final String DATABASE_NAME = "dbToDo";
	public static final String DATABASE_TABLE = "mainToDo";
	public static final int DATABASE_VERSION = 2;


	// SQL statement to create database
	private static final String DATABASE_CREATE_SQL =
		"CREATE TABLE " + DATABASE_TABLE
		+ " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ KEY_TASK + " TEXT NOT NULL, "
		+ KEY_DATE + " TEXT"
		+ ");";

	private final Context context;
	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version " + oldVersion
				+ " to" + newVersion + ", which will destroy all old data!");

			// destroys old database
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

			// recreate new database
			onCreate(_db);
		}
	}

	//constructor: initializes context
	public DBAdapter(Context ctx) { 
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}

	// open the data base connection
	public DBAdapter open () {
		db = myDBHelper.getWritableDatabase();
		return this;
	}

	//close the data base connection
	public void close() {
		myDBHelper.close();
	}

	// add a new set of values to be inserted into the database
	public long insertRow(String task, String date) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TASK, task);
		initialValues.put(KEY_DATE, date);

		// insert the data into the database
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	// delete a row from the database, by rowID (primary key)
	public boolean deleteRow(long rowID) {
		String where = KEY_ROWID + "-" + rowID;
		return db.delete(DATABASE_TABLE, WHERE, NULL) != 0;
	}

	public void deleteAll() {
		Cursor c = getAllRows();
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRow(c.getLong((int) rowId));
			} while (c.moveToNext());
		}
		c.close();
	}

	// return all data in the database
	public Cursor getAllRows() {
		String where = null;
		Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS, 
						where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// get a specific row by row id
	public Cursor getRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
						where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// change an existing or to be equal to new data
	public boolean updateRow(long rowId, String task, String date) {
		String where = KEY_ROWID + "=" + rowId;
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_TASK, task);
		newValues.put(KEY_DATE, date);
		// insert it into the database
		return db.update(DATABASE_TABLE, newValues, where, null) !=0;
	}


}