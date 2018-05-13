package com.example.mathwiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Callum on 12/05/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "HighScores.db";
    private static final String TABLE_NAME = "highScores";
    private static final String COL0 = "ID";
    private static final String COL1 = "Points";
    private static final int DATABASE_VERSION = 1;

    // _id integer primary key,
    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL1 + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public boolean addData(String item){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, item);

        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_NAME);

        long result = database.insert(TABLE_NAME, null, contentValues);

        // if data as inserted incorreclt it will return -1
        if(result == -1){
            return false;
        } else{
            return true;
        }
    }

    // Returns all the data from the database
    public Cursor getData(){
        SQLiteDatabase database = this.getWritableDatabase();

        final String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = database.rawQuery(query, null);
        return data;
    }

    public void deleteAll(){
        // clears all entries from the table
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM "+ TABLE_NAME);
    }
}

