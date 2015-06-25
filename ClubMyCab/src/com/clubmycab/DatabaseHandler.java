package com.clubmycab;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.clubmycab.utility.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "ClubMyCabDB";

	// Contacts table name
	private static final String TABLE_GROUPCHAT = "GROUPCHATTB";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_CABID = "CabId";
	private static final String KEY_SENDER = "Sender";
	private static final String KEY_MESSAGE = "Message";
	private static final String KEY_DATETIME = "DateTime";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_GROUPCHAT_TABLE = "CREATE TABLE " + TABLE_GROUPCHAT + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_CABID + " TEXT,"
				+ KEY_SENDER + " TEXT," + KEY_MESSAGE + " TEXT," + KEY_DATETIME
				+ " TEXT" + ")";

		db.execSQL(CREATE_GROUPCHAT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPCHAT);

		// Create tables again
		onCreate(db);
	}

	void addChattodb(ChatObject obj) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CABID, obj.getCabId().toString().trim());
		values.put(KEY_SENDER, obj.getFullName().toString().trim());
		values.put(KEY_MESSAGE, obj.getText().toString().trim());
		values.put(KEY_DATETIME, obj.getDatetime().toString().trim());

		// Inserting Row
		db.insert(TABLE_GROUPCHAT, null, values);
		db.close(); // Closing database connection
	}

	public List<ChatObject> getAllCabIdChats(String CabId) {
		List<ChatObject> chatlist = new ArrayList<ChatObject>();
		// Select All Query

		String selectQuery = "SELECT * FROM " + TABLE_GROUPCHAT + " WHERE "
				+ KEY_CABID + " = " + "'" + CabId + "'";

		Log.d("selectQuery", "" + selectQuery);

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ChatObject obj = new ChatObject();
				obj.setID(Integer.parseInt(cursor.getString(0)));
				obj.setCabId(cursor.getString(1));
				obj.setFullName(cursor.getString(2));
				obj.setText(cursor.getString(3));
				obj.setDatetime(cursor.getString(4));
				// Adding contact to list
				chatlist.add(obj);
			} while (cursor.moveToNext());
		}

		return chatlist;
	}

	public void deleteArchieveChats(String CabIds) {

		String selectQuery = "DELETE FROM " + TABLE_GROUPCHAT + " WHERE "
				+ KEY_CABID + " NOT IN " + "(" + CabIds + ")";
		Log.d("selectQuery", "" + selectQuery);
		getWritableDatabase().execSQL(selectQuery);
	}
}
