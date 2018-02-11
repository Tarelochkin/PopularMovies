package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.SyncMovieTask;
import com.example.android.popularmovies.data.PopularMoviesContract.MovieEntry;

/**
 * Created by РЕПКА on 11.12.2017.
 */

public class PopularMoviesProvider extends ContentProvider {

    private final static String LOG_TAG = PopularMoviesProvider.class.getSimpleName();

    PopularMoviesDbHelper mDbHelper;

    private static final int MOVIES = 100;
    private static final int MOVIE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.ITEMS_PATH, MOVIES);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.ITEMS_PATH + "/#", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PopularMoviesDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                break;
            case MOVIE_ID:
                selection = MovieEntry.COLUMN_TMDID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                break;
            default:
                throw new IllegalArgumentException("Unable to get data for " + uri);
        }

        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                long id = db.insert(MovieEntry.TABLE_NAME, null, values);

                Uri itemUri = ContentUris.withAppendedId(uri, id);
                getContext().getContentResolver().notifyChange(uri, null);
                return itemUri;
            default:
                throw new IllegalArgumentException("Can't create item with uri " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(LOG_TAG, "PROVIDER_DELETE");
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (match) {
            case MOVIE_ID:
                selection = MovieEntry.COLUMN_TMDID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
            default: throw new IllegalArgumentException("Unable to delete row for movie #" + selectionArgs[0]);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (match) {
//            case MOVIES:
//                rowsUpdated = db.update(
//                        MovieEntry.TABLE_NAME,
//                        values,
//                        selection,
//                        selectionArgs
//                );
//                break;
            case MOVIE_ID:
                selection = MovieEntry.COLUMN_TMDID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(
                        MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );

                if (rowsUpdated > 0) {
                    getContext().getContentResolver().notifyChange(
                            PopularMoviesContract.FULL_CONTENT_URI,
                            null);
                }
                return rowsUpdated;

            default: throw new IllegalArgumentException("Unable to update movie with id " + selectionArgs[0]);
        }

//        if (rowsUpdated > 0) {
//            getContext().getContentResolver().notifyChange(
//                    PopularMoviesContract.FULL_CONTENT_URI,
//                    null);
//        }

    }
}
