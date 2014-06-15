package com.example.avisreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.avisreader.data.Newspaper;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance = null;

    private static final String DATABASE_NAME = "newspapers_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NEWSPAPER = "newspapers";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_URL = "url";
    private static final String KEY_ISFAVORITE = "favorite";

    private static final String[] COLUMNS_NEWSPAPER = {KEY_ID, KEY_TITLE, KEY_URL, KEY_ISFAVORITE};

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_NEWSPAPER_TABLE = "CREATE TABLE " + TABLE_NEWSPAPER + "( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TITLE + " TEXT, " +
                KEY_URL + " TEXT, " +
                KEY_ISFAVORITE + " INTEGER )";

        db.execSQL(CREATE_NEWSPAPER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWSPAPER);
        onCreate(db);
    }

    public int addNewspaper(Newspaper np) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, np.getTitle());
        values.put(KEY_URL, np.getUrl());
        values.put(KEY_ISFAVORITE, (np.isFavorite() ? 1 : 0));

        int rowId = safeLongToInt(db.insert(TABLE_NEWSPAPER, null, values));
        db.close();

        return rowId;

    }

    public boolean deleteNewspaper(Newspaper np) {
        return false;
    }

    public void updateNewspaper(Newspaper np) {

    }


    public Newspaper getNewspaper(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NEWSPAPER, COLUMNS_NEWSPAPER,
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        Newspaper np = new Newspaper();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.d("APP", cursor.getString(0));
            Log.d("APP", cursor.getString(1));
            Log.d("APP", cursor.getString(2));
            Log.d("APP", cursor.getString(3));

            np = new Newspaper(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                    (Integer.parseInt(cursor.getString(3)) == 1 ? true : false));

            cursor.moveToNext();
        }

        db.close();
        cursor.close();
        return np;
    }

    public List<Newspaper> getAllNewspapers() {
        return null;
    }

    // Helper methods
    private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
