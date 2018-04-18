package com.example.mike.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mike.popmovies.data.MovieDbContract.MovieDbEntry;

/**
 * Created by Mike on 2/23/2018.
 */

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "popmovies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_POPMOVIES_TABLE = "CREATE TABLE " + MovieDbEntry.TABLE_NAME + " (" +
                MovieDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieDbEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieDbEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieDbEntry.COLUMN_MOVIE_RELEASE_DATE + " TIMESTAMP," +
                MovieDbEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL," +
                MovieDbEntry.COLUMN_MOVIE_OVERVIEW + " TEXT" +
                "); ";

        db.execSQL(SQL_CREATE_POPMOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieDbEntry.TABLE_NAME);
        onCreate(db);
    }
}
