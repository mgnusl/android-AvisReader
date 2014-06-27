package com.example.avisreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.avisreader.data.Newspaper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
    private static final String KEY_ICON = "icon";
    private static final String KEY_ICON_BLOB = "icon_blob";

    private static final String[] COLUMNS_NEWSPAPER = {KEY_ID, KEY_TITLE, KEY_URL, KEY_ISFAVORITE, KEY_ICON, KEY_ICON_BLOB};

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
                KEY_ISFAVORITE + " INTEGER, " +
                KEY_ICON + " TEXT, " +
                KEY_ICON_BLOB + " BLOB )";

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
        values.put(KEY_ICON, np.getIcon());

        // Icon bitmap
        if (np.getIconBitmap() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            np.getIconBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageByteArray = stream.toByteArray();
            values.put(KEY_ICON_BLOB, imageByteArray);
        }

        int rowId = safeLongToInt(db.insert(TABLE_NEWSPAPER, null, values));
        db.close();
        return rowId;

    }

    public boolean deleteNewspaper(Newspaper np) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = db.delete(TABLE_NEWSPAPER, KEY_ID + " = ?", new String[]{String.valueOf(np.getId())}) > 0;
        db.close();
        return success;
    }

    public int updateNewspaper(Newspaper np) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, np.getTitle());
        values.put(KEY_URL, np.getUrl());
        values.put(KEY_ISFAVORITE, (np.isFavorite() ? 1 : 0));
        values.put(KEY_ICON, np.getIcon());

        // Icon bitmap
        if(np.getIconBitmap() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            np.getIconBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageByteArray = stream.toByteArray();
            values.put(KEY_ICON_BLOB, imageByteArray);
        }


        return db.update(TABLE_NEWSPAPER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(np.getId())});

    }

    /**
     * Method for updating the "favorite" field of a row. Use this instead of "updateNewspaper" when
     * only the "favorite" field is updated to avoid losing icon quality with repeated writes of a row.
     * @param np
     * @return Whether or not a row was updated
     */
    public boolean updateNewspaperFavorite(Newspaper np) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(np.isFavorite()) {
            values.put(KEY_ISFAVORITE, 1);
        }
        else if(!np.isFavorite()) {
            values.put(KEY_ISFAVORITE, 0);
        }

        return db.update(TABLE_NEWSPAPER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(np.getId())}) > 0;
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

            if(!(cursor.getBlob(5) == null)) {
                Bitmap bm = convertByteArrayToBitmap(cursor.getBlob(5));
                np = new Newspaper(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                        (Integer.parseInt(cursor.getString(3)) == 1 ? true : false), cursor.getString(4), bm);
            }
            else {
                np = new Newspaper(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                        (Integer.parseInt(cursor.getString(3)) == 1 ? true : false), cursor.getString(4));
            }



            cursor.moveToNext();
        }

        db.close();
        cursor.close();
        return np;
    }

    public List<Newspaper> getAllNewspapers() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NEWSPAPER;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Newspaper> newsPaperList = new ArrayList<Newspaper>();

        if (cursor.moveToFirst()) {
            do {
                Newspaper np = new Newspaper();
                np.setId(Integer.parseInt(cursor.getString(0)));
                np.setTitle(cursor.getString(1));
                np.setUrl(cursor.getString(2));
                np.setFavorite((Integer.parseInt(cursor.getString(3)) == 1) ? true : false);
                np.setIcon(cursor.getString(4));

                if (cursor.getBlob(5) != null) {
                    Bitmap bm = convertByteArrayToBitmap(cursor.getBlob(5));
                    np.setIconBitmap(bm);
                }

                newsPaperList.add(np);
            } while (cursor.moveToNext());
        }

        return newsPaperList;
    }

    // Helper methods
    private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArrayToBeCOnvertedIntoBitMap) {
        Bitmap bitMapImage = BitmapFactory.decodeByteArray(
                byteArrayToBeCOnvertedIntoBitMap, 0,
                byteArrayToBeCOnvertedIntoBitMap.length);
        return bitMapImage;
    }
}
