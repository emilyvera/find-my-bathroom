package edu.illinois.cs465.findmybathroom;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "bathrooms.db";
    public static final String TABLE_NAME = "bathroom_data";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "LOCATION_TYPE";
    public static final String COL_3 = "LATITUDE";
    public static final String COL_4 = "LONGITUDE";
    public static final String COL_5 = "BUILDING_NAME";
    public static final String COL_6 = "IS_ALL_GENDER";
    public static final String COL_7 = "IS_WHEELCHAIR_ACCESSIBLE";
    public static final String COL_8 = "HAS_DIAPER_STATION";
    public static final String COL_9 = "RATING";
    public static final String COL_10 = "TOTAL_VOTES";

    // possible extra fields
    public static final String is_free = "bathroom_data.db";
    public static final String is_open_24_7 = "bathroom_data.db";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "LOCATION_TYPE TEXT," +
                "LATITUDE REAL," +
                "LONGITUDE REAL," +
                "BUILDING_NAME TEXT," +
                "IS_ALL_GENDER INTEGER," +
                "IS_WHEELCHAIR_ACCESSIBLE INTEGER," +
                "HAS_DIAPER_STATION INTEGER," +
                "RATING REAL," +
                "TOTAL_VOTES INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String location_type, Double latitude, Double longitude,
                              String building_name, int is_all_gender, int is_wheelchair_accessible,
                              int has_diaper_stations) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, location_type);
        contentValues.put(COL_3, latitude);
        contentValues.put(COL_4, longitude);
        contentValues.put(COL_5, building_name);
        contentValues.put(COL_6, is_all_gender);
        contentValues.put(COL_7, is_wheelchair_accessible);
        contentValues.put(COL_8, has_diaper_stations);
        contentValues.put(COL_9, 0);
        contentValues.put(COL_10, 0);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        return true;
    }

    @SuppressLint("Range")
    public void updateRating(int id, float newRating) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM bathroom_data WHERE ID=" + id, null);
        float oldRating = 0;
        int totalVotes = 0;
        if (cursor.moveToFirst()) {
            oldRating = cursor.getFloat(cursor.getColumnIndex("RATING"));
            totalVotes = cursor.getInt(cursor.getColumnIndex("TOTAL_VOTES"));
        }
        cursor.close();

        int updatedTotalVotes = totalVotes + 1;
        float updatedRating = (oldRating + newRating) / updatedTotalVotes;
        Log.v("updatedaTotalVotes", String.valueOf(updatedTotalVotes));
        Log.v("updatedRating", String.valueOf(updatedRating));
        Log.v("id is", String.valueOf(id));
        db.execSQL("UPDATE bathroom_data SET RATING=" + updatedRating + " WHERE ID=" + id);
        db.execSQL("UPDATE bathroom_data SET TOTAL_VOTES=" + updatedTotalVotes + " WHERE ID=" + id);
    }
}
