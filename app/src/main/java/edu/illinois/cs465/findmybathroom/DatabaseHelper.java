package edu.illinois.cs465.findmybathroom;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                "HAS_DIAPER_STATION INTEGER)");
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

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        return true;
    }
}
