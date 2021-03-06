package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.data.PopularMoviesContract.MovieEntry;

/**
 * Created by РЕПКА on 11.12.2017.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorites.db";
    public static final int DATABASE_VERSION = 6;

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MovieEntry.COLUMN_TMDID + " INTEGER DEFAULT 0, " +
            MovieEntry.COLUMN_NAME + " TEXT DEFAULT '', " +
            MovieEntry.COLUMN_IMAGE + " TEXT DEFAULT '', " +
            MovieEntry.COLUMN_YEAR + " TEXT DEFAULT '', " +
            MovieEntry.COLUMN_DURATION + " INTEGER DEFAULT 0, " +
            MovieEntry.COLUMN_RATING + " REAL DEFAULT 0, " +
            MovieEntry.COLUMN_SYNOPSIS + " TEXT DEFAULT '', " +
            MovieEntry.COLUMN_TRAILERS + " TEXT DEFAULT '', " +
            MovieEntry.COLUMN_REVIEWS + " TEXT DEFAULT '')";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }
}
