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
        //3rd argument to be passed is CursorFactory instance
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

        // Inserting Row
        db.insert(TABLE_STATIONS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single station
    Station getStation(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STATIONS, new String[] { KEY_ID,
                        KEY_STAION_ID, KEY_AIRPORT_NAME, KEY_DATA, KEY_DECODED }, KEY_STAION_ID + "='"+ id +"' OR "+ KEY_DECODED + " like" + "'%" + id + "%'", null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Station station = new Station(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        // return station
        return station;
    }
    // code to get the single station
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

    // code to get all stations in a list view
    public List<Station> getAllStations() {
        List<Station> stationList = new ArrayList<Station>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STATIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Station station = new Station();
                station.setID(Integer.parseInt(cursor.getString(0)));
                station.setStationId(cursor.getString(1));
                station.setAirportName(cursor.getString(2));
                station.setData(cursor.getString(3));
                station.setDecoded(cursor.getString(4));
                // Adding station to list
                stationList.add(station);
            } while (cursor.moveToNext());
        }

        // return station list
        return stationList;
    }

    // code to update the single station
    public int updateStation(Station station) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STAION_ID, station.getStationId()); // Station id
        values.put(KEY_AIRPORT_NAME, station.getAirportName()); // Airport name
        if(!station.getData().equals("none")){
            values.put(KEY_DATA, station.getData()); // raw data
        }
        values.put(KEY_DECODED, station.getDecoded()); // decoded data

        // updating row
        return db.update(TABLE_STATIONS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(station.getID()) });
    }
}
