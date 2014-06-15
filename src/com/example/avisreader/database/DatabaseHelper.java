package com.example.avisreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.avisreader.data.Newspaper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance = null;

    private static final String DATABASE_NAME = "newspapers_db";
    private static final int DATABASE_VERSION = 1;

    private static final String NEWSPAPER_TABLE_NAME = "newspapers";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_URL = "url";
    private static final String KEY_ISFAVORITE = "favorite";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("APP", "CONSTRUCTOR");

    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        Log.d("APP", "GETINSTANCE");

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("APP", "ONCREATE");
        Log.d("APP", "ONCREATE");
        Log.d("APP", "ONCREATE");
        Log.d("APP", "ONCREATE");


        String CREATE_NEWSPAPER_TABLE = "CREATE TABLE " + NEWSPAPER_TABLE_NAME + "( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TITLE + " TEXT, " +
                KEY_URL + " TEXT, " +
                KEY_ISFAVORITE + " INTEGER )";

        db.execSQL(CREATE_NEWSPAPER_TABLE);

        Log.d("APP", CREATE_NEWSPAPER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NEWSPAPER_TABLE_NAME);
        onCreate(db);
    }

    public int addNewspaper(Newspaper np) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, np.getTitle());
        values.put(KEY_URL, np.getUrl());
        values.put(KEY_ISFAVORITE, (np.isFavorite() ? 1 : 0));

        return safeLongToInt(db.insert(NEWSPAPER_TABLE_NAME, null, values));

    }

    public boolean deleteNewspaper(Newspaper np) {
        return false;
    }

    public void updateNewspaper(Newspaper np) {

    }

    public Newspaper getNewspaper(int newspaperID) {
        return null;
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
