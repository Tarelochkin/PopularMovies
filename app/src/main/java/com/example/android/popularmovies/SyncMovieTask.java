package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.data.PopularMoviesContract.MovieEntry;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.List;

/**
 * Created by РЕПКА on 26.01.2018.
 */

public class SyncMovieTask {

    private final static String LOG_TAG = SyncMovieTask.class.getSimpleName();

    public static final int SYNC_TO_ADD = 32;
    public static final int SYNC_TO_UPDATE = 33;

    synchronized public static void updateMovie(int tmdbId, Context context) {
        List<Movie> list = NetworkUtils.fetchMovieData(tmdbId, null);
        if (list.size() > 0) {
            syncMovie(list.get(0), context, SYNC_TO_UPDATE);
        }
    }

    synchronized public static long syncMovie(Movie movie, Context context, int syncType) {
        long id = -1;
        ContentValues values = new ContentValues();

        StringBuilder stringBuilder = new StringBuilder();
        String trailersString = null;
        String reviewsString = null;

        int movieId = movie.getId();
        String title = movie.getTitle();
        String synopsis = movie.getSynopsis();
        double rating = movie.getRating();
        String rawReleaseDate = movie.getReleaseDate();
        int rawRuntime = movie.getRuntime();
        String rawPosterPath = movie.getRawPosterPath();
        List<String[]> trailersList = movie.getTrailers();
        List<String[]> reviewsList = movie.getReviews();

        if (trailersList.size() > 0) {
            for (int i = 0; i < trailersList.size(); i++) {
                String[] currentTrailer = trailersList.get(i);
                stringBuilder.append(currentTrailer[0])
                        .append(DetailsActivity.YOUTUBE_KEY_SEPARATOR)
                        .append(currentTrailer[1])
                        .append(DetailsActivity.TRAILER_NAME_SEPARATOR);
            }
            trailersString = stringBuilder.toString();
            stringBuilder.setLength(0);
        }

        if (reviewsList.size() > 0) {
            for (int i = 0; i < reviewsList.size(); i++) {
                String[] currentReview = reviewsList.get(i);
                stringBuilder.append(currentReview[0])
                        .append(DetailsActivity.REVIEW_AUTHOR_SEPARATOR)
                        .append(currentReview[1])
                        .append(DetailsActivity.REVIEW_BODY_SEPARATOR);
            }
            reviewsString = stringBuilder.toString();
        }

        putIntIfNotZero(values, MovieEntry.COLUMN_TMDID, movieId);
        putStringIfNotEmpty(values, MovieEntry.COLUMN_NAME, title);
        putStringIfNotEmpty(values, MovieEntry.COLUMN_IMAGE, rawPosterPath);
        putStringIfNotEmpty(values, MovieEntry.COLUMN_YEAR, rawReleaseDate);
        putIntIfNotZero(values, MovieEntry.COLUMN_DURATION, rawRuntime);
        putStringIfNotEmpty(values, MovieEntry.COLUMN_SYNOPSIS, synopsis);
        putStringIfNotEmpty(values, MovieEntry.COLUMN_TRAILERS, trailersString);
        putStringIfNotEmpty(values, MovieEntry.COLUMN_REVIEWS, reviewsString);
        if (rating > 0) {
            values.put(MovieEntry.COLUMN_RATING, rating);
        }

        if (syncType == SYNC_TO_ADD) {
            Uri newItemUri = context.getContentResolver().insert(PopularMoviesContract.FULL_CONTENT_URI, values);
            id = ContentUris.parseId(newItemUri);
        } else {
            Log.d(LOG_TAG, "MOVIE ID IS " + movieId);
            String selection = MovieEntry.COLUMN_TMDID + "=?";
            String[] selectionArgs = new String[]{movieId + ""};
            int moviesUpdated = context.getContentResolver().update(
                    PopularMoviesContract.FULL_CONTENT_URI,
                    values,
                    selection,
                    selectionArgs
            );
            Log.d(LOG_TAG, "UPDATED " + moviesUpdated + " MOVIE(S)");
        }
        return id;
    }

    private static void putStringIfNotEmpty(ContentValues values, String tableColumn, String value) {
        if (value != null && !value.isEmpty()) {
            values.put(tableColumn, value);
        }
    }

    private static void putIntIfNotZero(ContentValues values, String tableColumn, int value) {
        if (value != 0) {
            values.put(tableColumn, value);
        }
    }
}
