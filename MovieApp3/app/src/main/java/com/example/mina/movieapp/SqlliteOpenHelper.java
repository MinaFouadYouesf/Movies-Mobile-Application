package com.example.mina.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class SqlliteOpenHelper extends SQLiteOpenHelper {
    private static final String Database_Name = "MovieData";
    private static final int Database_Version = 1;

    public SqlliteOpenHelper(Context context) {
        super(context, Database_Name, null, Database_Version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("data", "created");
        // create movies table
        db.execSQL("CREATE  TABLE IF NOT EXISTS movies ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older movies table if existed
        db.execSQL("DROP TABLE IF EXISTS movies");

        // create fresh movies table
        this.onCreate(db);
    }


    // Books table name
    private static final String TABLE_CONTACTS = "movies";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    // add  (insert)
    public void addContact(String movieName) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create movie values to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, movieName); // get name
        // 3. insert
        db.insert(TABLE_CONTACTS, // table
                null,
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    // get all (Select all)
    public ArrayList<String> getAllMovies() {
        ArrayList<String> movieNames = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_CONTACTS;

        // 2. get reference to Readable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        //Log.d("data", "1111111");

        if (cursor.moveToFirst()) {    // 2 func a) check if cursor empty or not then move it to the first row
            do {

                movieNames.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }


        db.close();
        return movieNames;
    }
    /*
    delte all data every update
     */
    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, null,null);
    }
}
