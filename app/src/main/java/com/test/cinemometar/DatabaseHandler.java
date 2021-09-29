package com.test.cinemometar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "metarBrowser";
    private static final String TABLE_STATIONS = "stations";
    private static final String KEY_ID = "id";
    private static final String KEY_STAION_ID = "station_id";
    private static final String KEY_AIRPORT_NAME = "airport_name";
    private static final String KEY_DATA = "data";
    private static final String KEY_DECODED = "decoded";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STATIONS_TABLE = "CREATE TABLE " + TABLE_STATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_STAION_ID + " TEXT,"
                + KEY_AIRPORT_NAME + " TEXT" + ","
                + KEY_DATA + " TEXT" + ","
                + KEY_DECODED + " TEXT" + ")";
        db.execSQL(CREATE_STATIONS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new station
    void addStation(Station station) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STAION_ID, station.getStationId()); // Station id
        values.put(KEY_AIRPORT_NAME, station.getAirportName()); // Airport name
        values.put(KEY_DATA, station.getData()); // raw data
        values.put(KEY_DECODED, station.getDecoded()); // decoded data

        // Insert Row
        db.insert(TABLE_STATIONS, null, values);
        db.close(); // Close database connection
    }

    // code to get the single station data
    Station getStation(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STATIONS, new String[] { KEY_ID,
                        KEY_STAION_ID, KEY_AIRPORT_NAME, KEY_DATA, KEY_DECODED }, KEY_STAION_ID + "='"+ id +"' OR "+ KEY_DECODED + " like" + "'%" + id + "%'", null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Station station = new Station(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        return station;
    }
    // code to get the check station if exist
    Boolean checkStation(String id) {

        String countQuery = "SELECT  * FROM " + TABLE_STATIONS + " WHERE " + KEY_STAION_ID + " = '" + id +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor != null) {
            cursor.close();
            return false;
        }
        return true;
    }

    // code to update the single station data
    public int updateStation(Station station) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STAION_ID, station.getStationId()); // Station id
        values.put(KEY_AIRPORT_NAME, station.getAirportName()); // Airport name
        if(!station.getData().equals("none")){
            values.put(KEY_DATA, station.getData()); // raw data
        }
        values.put(KEY_DECODED, station.getDecoded()); // decoded data

        // updating data
        return db.update(TABLE_STATIONS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(station.getID()) });
    }
}
