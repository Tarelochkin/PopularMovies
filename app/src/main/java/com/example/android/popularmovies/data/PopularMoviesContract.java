package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by РЕПКА on 11.12.2017.
 */

public class PopularMoviesContract {

    private PopularMoviesContract() {

    }

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String ITEMS_PATH = "movies";
    public static final Uri FULL_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, ITEMS_PATH);

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TMDID = "tmdId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_TRAILERS = "trailers";
        public static final String COLUMN_REVIEWS = "reviews";
    }
}
