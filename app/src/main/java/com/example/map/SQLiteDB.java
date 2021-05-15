package com.example.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class SQLiteDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "CoordData";

    private static final String TABLE_COORD= "Coord";

    private static final String KEY_ID = "id";
    private static final String LAT = "Lat";
    private static final String LON = "Lon";

    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        String CREATE_COORD_TABLE = "CREATE TABLE " + TABLE_COORD + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + LAT + " DOUBLE,"
                + LON + " DOUBLE" + ")";
        db.execSQL(CREATE_COORD_TABLE);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORD);
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public int addCoord(Coord Coord) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LAT, Coord.getLat()); // Coord Name
        values.put(LON, Coord.getLon()); // Coord Population

        long id = db.insert(TABLE_COORD, null, values);
        db.close();
        return (int) id;
    }

    Coord getCoord(int id) {
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_COORD, new String[] { KEY_ID, LAT, LON},
                KEY_ID + "= ?", new String[] { String.valueOf(id) } ,
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new Coord(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2));
    }

    public List<Coord> getAllCoord() {
        List<Coord> CoordList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_COORD;

        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                //LatLng tmp = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
                Coord tmp = new Coord();
                tmp.setId(cursor.getInt(0));
                tmp.setLat(cursor.getDouble(1));
                tmp.setLon(cursor.getDouble(2));
                CoordList.add(tmp);
            } while (cursor.moveToNext());
        }

        return CoordList;
    }

    public int updateCoord(Coord Coord) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LAT, Coord.getLat());
        values.put(LON, Coord.getLon());

        return db.update(TABLE_COORD, values, KEY_ID + " = ?",
                new String[] { String.valueOf(Coord.getId()) });
    }

    public void deleteCoord(Coord Coord) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COORD, KEY_ID + " = ?",
                new String[] { String.valueOf(Coord.getId()) });
        db.close();
    }

    public void deleteAllCoord() {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COORD,null,null);
        db.close();
    }

    public int getCoordCount() {
        String countQuery = "SELECT * FROM " + TABLE_COORD;
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int num =  cursor.getCount();
        cursor.close();

        return num;
    }
}
